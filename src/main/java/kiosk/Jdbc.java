package kiosk;

import java.sql.*;
import java.util.*;

/** Nimmt Verbindungen nur entgegen: Wer die Transaktion eröffnet, schließt sie. */
final class Jdbc {
    interface Zeile<T> { T lesen(ResultSet r) throws SQLException; }
    static void binden(PreparedStatement p, Object... werte) throws SQLException {
        for (int i = 0; i < werte.length; i++) p.setObject(i + 1, werte[i]);
    }
    static <T> List<T> liste(Connection c, String sql, Zeile<T> leser, Object... werte) throws SQLException {
        try (var p = c.prepareStatement(sql)) {
            binden(p, werte);
            try (var r = p.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (r.next()) result.add(leser.lesen(r));
                return result;
            }
        }
    }
    static <T> T eins(Connection c, String sql, Zeile<T> leser, Object... werte) throws SQLException {
        var zeilen = liste(c, sql, leser, werte);
        if (zeilen.isEmpty()) throw new Ablehnung("Kennung nicht gefunden.");
        if (zeilen.size() != 1) throw new SQLException("Genau eine Ergebniszeile erwartet.");
        return zeilen.getFirst();
    }
    static int schreiben(Connection c, String sql, Object... werte) throws SQLException {
        try (var p = c.prepareStatement(sql)) {
            binden(p, werte);
            return p.executeUpdate();
        }
    }
    static void genauEinmal(int zeilen) throws SQLException {
        if (zeilen != 1) throw new SQLException("Schreibschritt betraf " + zeilen + " statt einer Zeile.");
    }
}
