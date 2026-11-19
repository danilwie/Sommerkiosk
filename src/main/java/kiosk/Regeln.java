package kiosk;

import java.math.BigDecimal;
import java.time.*;

/** Fachliche Ablehnungen sind erwartete Bedienergebnisse, keine SQL-Fehler. */
class Ablehnung extends IllegalArgumentException {
    Ablehnung(String text) { super(text); }
}

final class Regeln {
    static final ZoneId ZONE = ZoneId.of("Europe/Berlin");
    static final int TAGESLIMIT = 150;
    static String name(String text) {
        String n = text.strip();
        if (n.isEmpty() || n.length() > 100 || n.chars().anyMatch(Character::isISOControl))
            throw new Ablehnung("Name: 1 bis 100 Zeichen ohne Steuerzeichen eingeben.");
        return n;
    }
    static int cent(String text) {
        String n = text.strip();
        if (!n.matches("[0-9]{1,8}([,.][0-9]{1,2})?"))
            throw new Ablehnung("Positiven Eurobetrag mit höchstens zwei Nachkommastellen eingeben.");
        try {
            int wert = new BigDecimal(n.replace(',', '.')).movePointRight(2).intValueExact();
            if (wert == 0) throw new Ablehnung("Preis muss positiv sein.");
            return wert;
        } catch (ArithmeticException e) { throw new Ablehnung("Preis außerhalb des Centbereichs."); }
    }
    static String euro(long cent) { return BigDecimal.valueOf(cent, 2).toPlainString().replace('.', ',') + " €"; }
    static void pruefeKauf(LocalDate start, LocalDate tag, boolean vorhanden, int preis, int rest, long heute) {
        if (tag.isBefore(start) || !tag.isBefore(start.plusDays(10))) throw new Ablehnung("Außerhalb der zehn Lagertage.");
        if (!vorhanden) throw new Ablehnung("Artikel nicht verfügbar.");
        if (preis > rest) throw new Ablehnung("Restbudget reicht nicht: " + euro(rest));
        if (heute + preis > TAGESLIMIT) throw new Ablehnung("Tageslimit: heute noch " + euro(TAGESLIMIT - heute));
    }
}
