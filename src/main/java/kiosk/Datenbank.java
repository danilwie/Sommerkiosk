package kiosk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import org.h2.tools.RunScript;

record Datenbank(String url) {
    Connection oeffnen() throws SQLException { return DriverManager.getConnection(url); }

    static void skript(Connection c, String pfad) throws SQLException {
        var stream = Datenbank.class.getResourceAsStream(pfad);
        if (stream == null) throw new SQLException("SQL-Ressource fehlt: " + pfad);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            RunScript.execute(c, reader);
        } catch (IOException e) { throw new SQLException("SQL-Ressource nicht lesbar", e); }
    }

    void schema() throws SQLException {
        try (var c = oeffnen()) { skript(c, "/db/schema.sql"); }
    }
}
