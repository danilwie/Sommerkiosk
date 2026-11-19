package kiosk;

import java.nio.file.Path;
import java.sql.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class KioskDbTest {
    static final LocalDate START = LocalDate.of(2026,7,1);
    Datenbank db;
    Connection anker;
    Stammdaten stamm;
    @BeforeEach void frisch() throws SQLException {
        db = new Datenbank("jdbc:h2:mem:" + UUID.randomUUID());
        anker = db.oeffnen();
        Datenbank.skript(anker,"/db/schema.sql");
        Datenbank.skript(anker,"/db/testdaten.sql");
        stamm = new Stammdaten(db);
    }
    @AfterEach void schliessen() throws SQLException { if (anker != null) anker.close(); }
    static Clock uhr(int tag) { return Clock.fixed(START.plusDays(tag-1).atTime(12,0).atZone(Regeln.ZONE).toInstant(),ZoneOffset.UTC); }
    KaufService am(int tag) { return new KaufService(db,START,uhr(tag)); }
    Abrechnung bericht(int tag) { return new Abrechnung(db,uhr(tag)); }
    // Unabhängige Kontrollabfragen: kein Service, kein gemeinsam verwendetes ResultSet.
    long zahl(String sql) throws SQLException {
        try (var c = db.oeffnen(); var s = c.createStatement(); var r = s.executeQuery(sql)) {
            assertTrue(r.next()); return r.getLong(1);
        }
    }
    void zustandLou(long anzahl,int rest) throws SQLException {
        assertEquals(anzahl,zahl("SELECT COUNT(*) FROM kauf WHERE kind_id=5"));
        assertEquals(rest,zahl("SELECT restbudget_cent FROM kind WHERE kind_id=5"));
    }
    @Test void stammdatenUndSuche() throws SQLException {
        long id = stamm.neuesKind("Mika");
        assertEquals(3,stamm.suche("iKa").size());
        assertEquals(1500,stamm.suche("Mika").getLast().rest());
        assertEquals(id,stamm.suche("Mika").getLast().id());
        stamm.neuesKind("50%_");
        assertEquals(1,stamm.suche("%_").size());
        assertTrue(stamm.suche("' OR 1=1 --").isEmpty());
    }
    @Test void ungueltigeStammdatenOhneSchreiben() throws SQLException {
        assertThrows(Ablehnung.class,()->stamm.neuesKind("  "));
        assertThrows(Ablehnung.class,()->stamm.neuerArtikel("Tee",0));
        assertEquals(5,zahl("SELECT COUNT(*) FROM kind")); assertEquals(5,stamm.sortiment().size());
    }
    @Test void artikelCrud() throws SQLException {
        long id = stamm.neuerArtikel("Tee",70);
        stamm.aendern(id,80,false);
        assertEquals(new Stammdaten.Artikel(id,"Tee",80,false),stamm.sortiment().getLast());
        stamm.loescheArtikel(id);
        assertEquals(5,stamm.sortiment().size());
        assertThrows(Ablehnung.class,()->stamm.loescheArtikel(id));
    }
    @Test void wirksameFremdschluessel() throws SQLException {
        assertThrows(Ablehnung.class,()->stamm.loescheArtikel(1));
        try (var s = anker.createStatement()) {
            assertEquals("23503",assertThrows(SQLException.class,()->s.executeUpdate("DELETE FROM kind WHERE kind_id=1")).getSQLState());
        }
        assertEquals(5,zahl("SELECT COUNT(*) FROM kauf")); assertEquals(5,stamm.sortiment().size());
    }
    @Test void commit() throws SQLException {
        assertEquals(new KaufService.Beleg(100,1400),am(1).buchen(5,1));
        zustandLou(1,1400); assertEquals(100,zahl("SELECT preis_cent FROM kauf WHERE kind_id=5"));
    }
    @Test void rollbackNachInsert() throws SQLException {
        zustandLou(0,1500);
        try (var s = anker.createStatement()) {
            s.execute("ALTER TABLE kind ADD CONSTRAINT test_update_stop CHECK(kind_id<>5 OR restbudget_cent=1500)");
        }
        var e = assertThrows(SQLException.class,()->am(1).buchen(5,1));
        assertEquals("23513",e.getSQLState()); assertTrue(e.getMessage().contains("TEST_UPDATE_STOP"));
        zustandLou(0,1500);
    }
    @Test void exaktLimitUndEinCentZuviel() throws SQLException {
        long cent = stamm.neuerArtikel("Probe",1);
        am(1).buchen(5,1); assertEquals(150,am(1).buchen(5,2).heute());
        assertThrows(Ablehnung.class,()->am(1).buchen(5,cent)); zustandLou(2,1350);
    }
    @Test void zehnTageBisNull() throws SQLException {
        for (int t=1;t<=9;t++) am(t).buchen(5,3);
        am(10).buchen(5,1); zustandLou(10,50);
        long zuTeuer = stamm.neuerArtikel("Probe",51);
        assertThrows(Ablehnung.class,()->am(10).buchen(5,zuTeuer)); zustandLou(10,50);
        am(10).buchen(5,2); zustandLou(11,0);
    }
    @Test void keineAnsparung() throws SQLException {
        long teuer = stamm.neuerArtikel("Set",151);
        assertThrows(Ablehnung.class,()->am(2).buchen(5,teuer)); zustandLou(0,1500);
    }
    @Test void mitternachtBerlin() throws SQLException {
        for (String zeit : List.of("2026-07-01T21:59:59.999999999Z","2026-07-01T22:00:00Z")) {
            var u = Clock.fixed(Instant.parse(zeit),ZoneOffset.UTC);
            assertEquals(150,new KaufService(db,START,u).buchen(5,3).heute());
            assertEquals(150,new Abrechnung(db,u).auszug(5).heute());
        }
        zustandLou(2,1200);
    }
    @Test void lagergrenzen() throws SQLException {
        for (int t : new int[]{0,11}) assertThrows(Ablehnung.class,()->am(t).buchen(5,1));
        zustandLou(0,1500); am(1).buchen(5,1); am(10).buchen(5,1); zustandLou(2,1300);
    }
    @Test void unbekanntUndGesperrt() throws SQLException {
        assertThrows(Ablehnung.class,()->am(1).buchen(5,5)); assertThrows(Ablehnung.class,()->am(1).buchen(5,99));
        assertThrows(Ablehnung.class,()->am(1).buchen(99,1)); assertThrows(Ablehnung.class,()->bericht(1).auszug(99));
        zustandLou(0,1500); assertEquals(5,zahl("SELECT COUNT(*) FROM kauf"));
    }
    @Test void historischePreise() throws SQLException {
        am(1).buchen(5,1); stamm.aendern(1,120,true); am(2).buchen(5,1);
        var x = bericht(2).auszug(5);
        assertEquals(List.of(100,120),x.positionen().stream().map(Abrechnung.Position::preis).toList());
        assertEquals(120,x.heute()); zustandLou(2,1280);
    }
    @Test void abrechnungMitUndOhneKaeufe() throws SQLException {
        assertEquals(0,bericht(1).auszug(5).saldo().ausgaben());
        assertEquals(1500,bericht(1).alle().getLast().rest());
        am(1).buchen(5,1); am(3).buchen(5,3);
        var x = bericht(3).auszug(5); assertEquals(250,x.saldo().ausgaben());
        assertEquals(150,x.heute()); assertEquals(1250,x.saldo().rest()); assertEquals(x.saldo(),bericht(3).alle().getLast());
    }
    @Test void manipulationInAbrechnungErkannt() throws SQLException {
        Jdbc.schreiben(anker,"UPDATE kind SET restbudget_cent=1400 WHERE kind_id=5");
        assertThrows(SQLException.class,()->bericht(1).auszug(5)); assertThrows(SQLException.class,()->bericht(1).alle());
    }
    @Test void wiederaufbau() throws SQLException {
        for (int i=0;i<2;i++) {
            var neu = new Datenbank("jdbc:h2:mem:"+UUID.randomUUID());
            try (var c=neu.oeffnen()) {
                Datenbank.skript(c,"/db/schema.sql"); Datenbank.skript(c,"/db/testdaten.sql");
                assertEquals(5,new Stammdaten(neu).sortiment().size());
                var salden = new Abrechnung(neu,uhr(1)).alle();
                assertEquals(5,salden.size()); assertEquals(325,salden.stream().mapToLong(Abrechnung.Saldo::ausgaben).sum());
                assertEquals(5,Jdbc.eins(c,"SELECT COUNT(*) FROM kauf",r->r.getInt(1)).intValue());
            }
        }
    }
    @Test void dateiNeustart(@TempDir Path ordner) throws SQLException {
        String url="jdbc:h2:file:"+ordner.resolve("kiosk");
        var erste = new Datenbank(url); erste.schema();
        var st = new Stammdaten(erste); long k=st.neuesKind("Jo"), a=st.neuerArtikel("Keks",100);
        new KaufService(erste,START,uhr(1)).buchen(k,a);
        var zweite = new Datenbank(url); zweite.schema();
        var x=new Abrechnung(zweite,uhr(1)).auszug(k); assertEquals(1,x.positionen().size()); assertEquals(1400,x.saldo().rest());
    }
    @Test void verbindungFehlt(@TempDir Path ordner) {
        var fehlt = new Datenbank("jdbc:h2:file:"+ordner.resolve("unbekannt")+";IFEXISTS=TRUE");
        var e = assertThrows(SQLException.class,()->new Stammdaten(fehlt).suche(""));
        assertNotNull(e.getSQLState());
    }
}
