package kiosk;

import java.sql.*;
import java.time.*;

record KaufService(Datenbank db, LocalDate start, Clock clock) {
    record Beleg(long heute, int rest) {}
    Beleg buchen(long kindId, long artikelId) throws SQLException {
        try (var c = db.oeffnen()) {
            c.setAutoCommit(false);
            try {
                var kind = Stammdaten.kind(c,kindId);
                var artikel = Stammdaten.artikel(c,artikelId);
                var jetzt = clock.instant().atZone(Regeln.ZONE);
                var tag = jetzt.toLocalDate();
                long heute = Jdbc.eins(c, """
                    SELECT COALESCE(SUM(preis_cent),0) FROM kauf
                    WHERE kind_id=? AND gekauft_am>=? AND gekauft_am<?
                    """, r -> r.getLong(1), kindId, tag.atStartOfDay(Regeln.ZONE).toOffsetDateTime(),
                    tag.plusDays(1).atStartOfDay(Regeln.ZONE).toOffsetDateTime());
                Regeln.pruefeKauf(start,tag,artikel.vorhanden(),artikel.preis(),kind.rest(),heute);
                Jdbc.genauEinmal(Jdbc.schreiben(c,"INSERT INTO kauf(kind_id,artikel_id,gekauft_am,preis_cent) VALUES (?,?,?,?)",
                    kindId,artikelId,jetzt.toOffsetDateTime(),artikel.preis()));
                int rest = kind.rest() - artikel.preis();
                Jdbc.genauEinmal(Jdbc.schreiben(c,"UPDATE kind SET restbudget_cent=? WHERE kind_id=? AND restbudget_cent=?",rest,kindId,kind.rest()));
                c.commit();
                return new Beleg(heute + artikel.preis(),rest);
            } catch (SQLException | RuntimeException e) {
                try { c.rollback(); } catch (SQLException rollbackFehler) { e.addSuppressed(rollbackFehler); }
                throw e;
            }
        }
    }
}
