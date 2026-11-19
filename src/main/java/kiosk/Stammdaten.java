package kiosk;

import java.sql.*;
import java.util.List;

record Stammdaten(Datenbank db) {
    record Kind(long id, String name, int rest) {}
    record Artikel(long id, String name, int preis, boolean vorhanden) {}
    static Kind kindZeile(ResultSet r) throws SQLException { return new Kind(r.getLong(1),r.getString(2),r.getInt(3)); }
    static Artikel artikelZeile(ResultSet r) throws SQLException { return new Artikel(r.getLong(1),r.getString(2),r.getInt(3),r.getBoolean(4)); }
    static Kind kind(Connection c, long id) throws SQLException {
        return Jdbc.eins(c,"SELECT kind_id,name,restbudget_cent FROM kind WHERE kind_id=?",Stammdaten::kindZeile,id);
    }
    static Artikel artikel(Connection c, long id) throws SQLException {
        return Jdbc.eins(c,"SELECT artikel_id,name,preis_cent,vorhanden FROM artikel WHERE artikel_id=?",Stammdaten::artikelZeile,id);
    }
    long neuesKind(String name) throws SQLException {
        return anlegen("INSERT INTO kind(name) VALUES (?)", Regeln.name(name));
    }
    long neuerArtikel(String name, int preis) throws SQLException {
        String n = Regeln.name(name);
        if (preis <= 0) throw new Ablehnung("Preis muss positiv sein.");
        return anlegen("INSERT INTO artikel(name,preis_cent) VALUES (?,?)", n, preis);
    }
    private long anlegen(String sql, Object... werte) throws SQLException {
        try (var c = db.oeffnen(); var p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Jdbc.binden(p,werte);
            Jdbc.genauEinmal(p.executeUpdate());
            try (var keys = p.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Generierte Kennung fehlt.");
                return keys.getLong(1);
            }
        }
    }
    List<Kind> suche(String teil) throws SQLException {
        try (var c = db.oeffnen()) {
            return Jdbc.liste(c,"SELECT kind_id,name,restbudget_cent FROM kind WHERE LOCATE(LOWER(?),LOWER(name))>0 ORDER BY kind_id",Stammdaten::kindZeile,teil);
        }
    }
    List<Artikel> sortiment() throws SQLException {
        try (var c = db.oeffnen()) {
            return Jdbc.liste(c,"SELECT artikel_id,name,preis_cent,vorhanden FROM artikel ORDER BY artikel_id",Stammdaten::artikelZeile);
        }
    }
    void aendern(long id, int preis, boolean verfuegbar) throws SQLException {
        if (preis <= 0) throw new Ablehnung("Preis muss positiv sein.");
        try (var c = db.oeffnen()) {
            if (Jdbc.schreiben(c,"UPDATE artikel SET preis_cent=?,vorhanden=? WHERE artikel_id=?",preis,verfuegbar,id)==0)
                throw new Ablehnung("Artikelkennung nicht gefunden.");
        }
    }
    void loescheArtikel(long id) throws SQLException {
        try (var c = db.oeffnen()) {
            if (Jdbc.schreiben(c,"DELETE FROM artikel WHERE artikel_id=?",id)==0) throw new Ablehnung("Artikelkennung nicht gefunden.");
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) throw new Ablehnung("Artikel hat Käufe und bleibt erhalten.");
            throw e;
        }
    }
}
