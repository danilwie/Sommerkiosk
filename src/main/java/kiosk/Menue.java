package kiosk;

import java.sql.*;
import java.util.Scanner;
import static kiosk.Regeln.euro;

record Menue(Datenbank db, KaufService kauf, Abrechnung abrechnung) {
    static final String HILFE = """
        kind|Name                 artikel|Name|Euro
        suche|Namensteil          liste
        aendern|Artikel-ID|Euro|ja/nein     loeschen|Artikel-ID
        kauf|Kind-ID|Artikel-ID   auszug|Kind-ID
        abschluss                 hilfe                 ende
        'suche|' zeigt alle Kinder. Bei 'kauf' separat mit 'ja' bestätigen.""";
    void start(Scanner in) {
        System.out.println(HILFE);
        while (in.hasNextLine()) {
            String[] f = in.nextLine().strip().split("\\|",-1);
            if (f.length == 1 && f[0].equals("ende")) return;
            try { ausfuehren(f,in); }
            catch (Ablehnung e) { System.out.println("Abgelehnt: " + e.getMessage()); }
            catch (SQLException e) { Main.technik(e); }
        }
    }
    private static void felder(String[] f, int n) {
        if (f.length != n) throw new Ablehnung("Falsche Feldzahl. Siehe hilfe; Trennzeichen ist |.");
    }
    private static long id(String s) {
        try { long n = Long.parseLong(s.strip()); if (n > 0) return n; }
        catch (NumberFormatException ignored) { /* Gemeinsame fachliche Meldung */ }
        throw new Ablehnung("Positive ganzzahlige Kennung erforderlich.");
    }
    private void ausfuehren(String[] f, Scanner in) throws SQLException {
        var stamm = new Stammdaten(db);
        switch (f[0]) {
            case "kind" -> { felder(f,2); System.out.println("Kind-ID: " + stamm.neuesKind(f[1])); }
            case "artikel" -> { felder(f,3); System.out.println("Artikel-ID: " + stamm.neuerArtikel(f[1],Regeln.cent(f[2]))); }
            case "suche" -> {
                felder(f,2); var treffer = stamm.suche(f[1]);
                if (treffer.isEmpty()) System.out.println("Keine Treffer.");
                for (var k : treffer) System.out.printf("%d | %s | Rest %s%n",k.id(),k.name(),euro(k.rest()));
            }
            case "liste" -> {
                felder(f,1);
                for (var a : stamm.sortiment()) System.out.printf("%d | %s | %s | %s%n",a.id(),a.name(),euro(a.preis()),a.vorhanden()?"verfügbar":"nicht verfügbar");
            }
            case "aendern" -> {
                felder(f,4);
                if (!f[3].equals("ja") && !f[3].equals("nein")) throw new Ablehnung("Verfügbarkeit: ja oder nein.");
                stamm.aendern(id(f[1]),Regeln.cent(f[2]),f[3].equals("ja")); System.out.println("Artikel geändert.");
            }
            case "loeschen" -> { felder(f,2); stamm.loescheArtikel(id(f[1])); System.out.println("Artikel gelöscht."); }
            case "kauf" -> {
                felder(f,3); long k = id(f[1]), a = id(f[2]);
                try (var c = db.oeffnen()) {
                    var kind = Stammdaten.kind(c,k); var artikel = Stammdaten.artikel(c,a);
                    System.out.printf("%s (#%d): %s für %s buchen? ja / nein%n",kind.name(),k,artikel.name(),euro(artikel.preis()));
                }
                if (!in.hasNextLine() || !in.nextLine().strip().equals("ja")) { System.out.println("Abgebrochen."); return; }
                var beleg = kauf.buchen(k,a);
                System.out.printf("Gebucht. Heute %s | Rest %s%n",euro(beleg.heute()),euro(beleg.rest()));
            }
            case "auszug" -> {
                felder(f,2); var x = abrechnung.auszug(id(f[1]));
                for (var p : x.positionen()) System.out.printf("%s | %s | %s%n",p.zeit().atZoneSameInstant(Regeln.ZONE),p.artikel(),euro(p.preis()));
                saldo(x.saldo()); System.out.println("Davon heute: " + euro(x.heute()));
            }
            case "abschluss" -> {
                felder(f,1); long summe = 0;
                for (var s : abrechnung.alle()) { saldo(s); summe += s.rest(); }
                System.out.println("Rückzahlungsbeträge gesamt (keine Auszahlung): " + euro(summe));
            }
            case "hilfe" -> System.out.println(HILFE);
            default -> throw new Ablehnung("Befehl unbekannt. Siehe hilfe.");
        }
    }
    private static void saldo(Abrechnung.Saldo s) {
        System.out.printf("#%d %s | Start %s | Ausgaben %s | Rückzahlungsbetrag %s%n",
            s.id(),s.name(),euro(s.start()),euro(s.ausgaben()),euro(s.rest()));
    }
}
