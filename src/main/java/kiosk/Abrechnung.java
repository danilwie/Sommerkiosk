package kiosk;

import java.sql.*;
import java.time.*;
import java.util.List;

record Abrechnung(Datenbank db, Clock clock) {
    record Saldo(long id, String name, int start, long ausgaben, int rest) {}
    record Position(OffsetDateTime zeit, String artikel, int preis) {}
    record Auszug(Saldo saldo, List<Position> positionen, long heute) {}
    private static Saldo geprueft(long id, String name, int start, long summe, int rest) throws SQLException {
        if (start - summe != rest) throw new SQLException("Budget und Kaufhistorie widersprechen sich bei Kind " + id);
        return new Saldo(id,name,start,summe,rest);
    }
    List<Saldo> alle() throws SQLException {
        try (var c = db.oeffnen()) {
            return Jdbc.liste(c, """
                SELECT k.kind_id,k.name,k.budget_cent,COALESCE(SUM(b.preis_cent),0),k.restbudget_cent
                FROM kind k LEFT JOIN kauf b ON k.kind_id=b.kind_id
                GROUP BY k.kind_id,k.name,k.budget_cent,k.restbudget_cent ORDER BY k.kind_id
                """, r -> geprueft(r.getLong(1),r.getString(2),r.getInt(3),r.getLong(4),r.getInt(5)));
        }
    }
    Auszug auszug(long id) throws SQLException {
        try (var c = db.oeffnen()) {
            var kind = Stammdaten.kind(c,id);
            var posten = Jdbc.liste(c, """
                SELECT b.gekauft_am,a.name,b.preis_cent FROM kauf b JOIN artikel a ON a.artikel_id=b.artikel_id
                WHERE b.kind_id=? ORDER BY b.gekauft_am,b.kauf_id
                """,r -> new Position(r.getObject(1,OffsetDateTime.class),r.getString(2),r.getInt(3)),id);
            var tag = LocalDate.now(clock.withZone(Regeln.ZONE));
            long summe = 0, heute = 0;
            for (var p : posten) {
                summe += p.preis();
                if (p.zeit().atZoneSameInstant(Regeln.ZONE).toLocalDate().equals(tag)) heute += p.preis();
            }
            return new Auszug(geprueft(id,kind.name(),1500,summe,kind.rest()),posten,heute);
        }
    }
}
