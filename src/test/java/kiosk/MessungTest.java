package kiosk;

import java.sql.*;
import java.util.*;
import org.h2.api.DatabaseEventListener;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessungTest {
    /** H2 meldet tatsächlich gestartete Statements, nicht die Anzahl der Ergebniszeilen. */
    public static class Beobachter implements DatabaseEventListener {
        static final ThreadLocal<List<String>> fenster = new ThreadLocal<>();
        @Override public void setProgress(int state,String sql,long x,long max) {
            var log = fenster.get();
            if (log != null && state == STATE_STATEMENT_START && sql.stripLeading().toUpperCase(Locale.ROOT).startsWith("SELECT"))
                log.add(sql);
        }
    }
    @Test void selectAnzahlAbschluss() throws SQLException {
        for (int anzahl : new int[]{5,50,500}) {
            var db = new Datenbank("jdbc:h2:mem:"+UUID.randomUUID()+";DATABASE_EVENT_LISTENER='kiosk.MessungTest$Beobachter'");
            try (var c=db.oeffnen()) {
                Datenbank.skript(c,"/db/schema.sql"); Datenbank.skript(c,"/db/testdaten.sql");
                var st = new Stammdaten(db); var service = new KaufService(db,KioskDbTest.START,KioskDbTest.uhr(3));
                for (int i=5;i<anzahl;i++) service.buchen(st.neuesKind("Messkind "+i),1);
                List<String> log = new ArrayList<>(); List<Abrechnung.Saldo> salden;
                Beobachter.fenster.set(log);
                try { salden = new Abrechnung(db,KioskDbTest.uhr(3)).alle(); }
                finally { Beobachter.fenster.remove(); }
                assertEquals(anzahl,salden.size());
                assertEquals(325L+(anzahl-5)*100L,salden.stream().mapToLong(Abrechnung.Saldo::ausgaben).sum());
                assertEquals(1,log.size(),log.toString());
                System.out.printf("MESSUNG Kinder=%d Käufe=%d SELECTs=%d%n",anzahl,anzahl,log.size());
            }
        }
    }
}
