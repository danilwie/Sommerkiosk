package kiosk;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

public final class Main {
    public static void main(String[] args) {
        try {
            boolean demo = Arrays.equals(args,new String[]{"--demo"});
            if (!demo && !(args.length == 2 && args[0].equals("--lager"))) {
                System.err.println("Start: --demo oder --lager JJJJ-MM-TT (erster Lagertag)");
                System.exit(2);
            }
            LocalDate start = demo ? LocalDate.of(2026,7,1) : LocalDate.parse(args[1]);
            Clock clock = demo ? Clock.fixed(Instant.parse("2026-07-03T10:00:00Z"),Regeln.ZONE) : Clock.system(Regeln.ZONE);
            String ort = demo ? "mem:demo" : "file:./data/kiosk-" + start;
            var db = new Datenbank("jdbc:h2:" + ort);
            // Hält die Demo-DB am Leben; im Dateimodus hält H2 die Dateisperre während der Sitzung.
            try (var anker = db.oeffnen(); var input = new Scanner(System.in)) {
                Datenbank.skript(anker,"/db/schema.sql");
                if (demo) Datenbank.skript(anker,"/db/demo.sql");
                System.out.println(demo ? "DEMO: eigener flüchtiger Bestand, Uhr fest." : "Nutzdaten: " + Path.of("data","kiosk-"+start+".mv.db").toAbsolutePath());
                System.out.printf("Lager %s bis %s | heute %s | Europe/Berlin%n",start,start.plusDays(9),LocalDate.now(clock));
                new Menue(db,new KaufService(db,start,clock),new Abrechnung(db,clock)).start(input);
            }
        } catch (DateTimeException e) {
            System.err.println("Ungültiges Lagerdatum. Erwartet: JJJJ-MM-TT."); System.exit(2);
        } catch (SQLException e) {
            technik(e); System.exit(1);
        }
    }
    static void technik(SQLException e) {
        System.err.println("Datenbankfehler. Pfad, Dateirechte und laufende Instanz prüfen. Vor erneutem Kauf den Auszug kontrollieren.");
        e.printStackTrace(System.err);
    }
}
