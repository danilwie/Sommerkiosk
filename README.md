# Sommerkiosk | Start, Test und Vorführung

Ein Kioskteam führt pro Kind ein Guthaben von 15,00 €. Pro Berliner Kalendertag sind höchstens 1,50 € erlaubt. Die Konsole verwaltet Stammdaten, verbucht einen Artikel je Kauf und berechnet den zurückzugebenden Betrag. Sie zahlt kein Geld aus. Stand 1.0.0, lokal geprüft am 19.11.2026. Präsentation: 25.11.2026. Offizieller Abgabetermin: nicht bekannt.

## Vom ZIP zur laufenden Anwendung

1. ZIP in einen neuen Ordner entpacken. Im Terminal in `Sommerkiosk` wechseln: Dort müssen `pom.xml`, `mvnw`, `.mvn` und `src` liegen. Versteckte Dateien beim Kopieren mitnehmen.
2. Vollständiges **JDK 25** installieren; geprüft mit Azul Zulu OpenJDK 25.0.2 auf Linux x86_64. `JAVA_HOME` auf das JDK-Verzeichnis setzen, dessen `bin` in `PATH` aufnehmen. `java -version` und `javac -version` müssen 25 melden. In IntelliJ IDEA oder Eclipse die `pom.xml` als Maven-Projekt öffnen, Projekt-SDK und Maven-JDK auf 25 stellen. Eine IDE ist zum Start im Terminal nicht nötig.
3. Der erste Build benötigt Internetzugriff auf Maven Central, Schreibrechte im Projekt und im Maven-Benutzerverzeichnis. Der mitgelieferte Maven Wrapper 3.3.2 bezieht Maven 3.9.9; Maven lädt H2 2.3.232 und JUnit Jupiter 5.11.4. Es ist kein Datenbankserver, keine separate Maven-Installation und kein Passwort erforderlich.
4. Im Projektordner ausführen (Linux/macOS):

```sh
sh ./mvnw -v
sh ./mvnw -B -ntp clean verify
java -jar target/sommerkiosk-1.0.0.jar --demo
```

Unter Windows PowerShell:

```powershell
.\mvnw.cmd -v
.\mvnw.cmd -B -ntp clean verify
java -jar target/sommerkiosk-1.0.0.jar --demo
```

Erwartet: `BUILD SUCCESS`, 24 Tests, keine Fehler oder übersprungenen Tests. Surefire führt die Klassen `RegelnTest` (5), `KioskDbTest` (18) und `MessungTest` (1 mit H2) in der Phase `test` aus. `verify` baut auch das startfähige JAR mit Treiber. Einzelberichte liegen unter `target/surefire-reports/`. Nach `--demo` zeigt die Konsole Hilfe und wartet auf eine Befehlszeile; `ende` beendet sie.

## Drei Demo-Abläufe in einer Sitzung

Die feste Demo-Uhr zeigt den **03.07.2026, 12:00 Uhr in Berlin**, unabhängig vom Präsentationstag. Die Daten gehören ausschließlich zu `jdbc:h2:mem:demo`. Die folgenden Abläufe in derselben frisch gestarteten Sitzung ausführen:

1. **Stammdaten:** `suche|Alex` findet zwei verschiedene IDs (1 und 2). `liste` zeigt fünf Artikel. `artikel|Probiertee|0,60` liefert ID 6. `aendern|6|0,70|ja` ändert den Preis, `loeschen|6` entfernt den unbenutzten Artikel. Artikel 2 mit Kaufhistorie dagegen darf nicht gelöscht werden.
2. **Kauf und Ablehnung:** `kauf|1|2`, dann auf einer separaten Zeile `ja`. Alex Nr. 1 hat heute schon 1,00 € ausgegeben; der Apfel zu 0,50 € schöpft das Limit exakt aus. Erwartet: heute 1,50 €, Rest 13,50 €. Jetzt `kauf|1|4` und `ja`: Wasser zu 0,25 € wird abgelehnt, die Werte bleiben gleich. Jede andere Bestätigungsantwort bricht ohne Buchung ab.
3. **Abrechnung:** `auszug|1` zeigt Saft und Apfel zu den damaligen Preisen. `abschluss` zeigt fünf geprüfte Rückzahlungsbeträge, zusammen **70,75 €**, keine Auszahlung. `ende`, dann erneut `--demo`: Jetzt ist die ursprüngliche Demo wiederhergestellt.

Automatischer Durchlauf mit genau diesen Eingaben, Linux/macOS:

```sh
java -jar target/sommerkiosk-1.0.0.jar --demo < demo/eingaben.txt
```

Windows PowerShell: `Get-Content demo/eingaben.txt | java -jar target/sommerkiosk-1.0.0.jar --demo`. Für die Qualitätsdemo auf beiden Plattformen: im folgenden Block unter Windows `sh ./mvnw` durch `.\mvnw.cmd` ersetzen.

```sh
sh ./mvnw -B -ntp "-Dtest=KioskDbTest#rollbackNachInsert" test
sh ./mvnw -B -ntp "-Dtest=MessungTest" test
```

## Nutzbetrieb und Datenerhalt

`--lager` erwartet das tatsächliche Startdatum als ISO-Datum. Beispiel **nur für einen isolierten Probelauf**, nicht als Festlegung des realen Lagerzeitraums:

```sh
java -jar target/sommerkiosk-1.0.0.jar --lager 2026-07-01
```

Hier verwendet die Anwendung die reale Uhr und `jdbc:h2:file:./data/kiosk-2026-07-01`. H2 speichert relativ zum Arbeitsverzeichnis in `data/kiosk-2026-07-01.mv.db`. Das eingegebene Datum bezeichnet zugleich ein eigenes Lager mit zehn Tagen. Gleiches Verzeichnis und gleiches Startdatum öffnen denselben Bestand; ein anderes Datum öffnet eine andere Datei. Die Anwendung zeigt den absoluten Dateipfad an. Der erste Start legt ausschließlich das Schema an; später werden vorhandene Daten weder gelöscht noch neu befüllt. Außerhalb der zehn Tage bleiben Verwaltung und Auskunft möglich, ein Kauf nicht. Kein Servermodus, keine parallelen Bediener, keine automatische Migration fremder oder alter Schemata.

Im leeren Lager: `kind|Alex`, `artikel|Apfel|0,50`, `suche|`, `liste`. Nur während des eingestellten Zeitraums `kauf|1|1` und `ja`. Eine Sitzungsunterbrechung löscht nichts. Zum Sichern erst `ende` eingeben, dann den gesamten Datenordner an einen geschützten Ort kopieren. Niemals persönliche Daten oder Sicherungen committen.

**Eingaben:** `|` trennt Felder und kann im Terminal nicht Teil eines Namens sein. Namen: 1 bis 100 UTF-16-Codeeinheiten nach Entfernen äußerer Leerzeichen; keine Steuerzeichen. IDs sind positive Ganzzahlen, Namen sind nicht eindeutig. Preise in Euro mit ein bis acht Vorkommastellen und optional einer oder zwei Nachkommastellen: `1`, `1,5`, `1.50`, `0,01`. Keine Tausendertrennzeichen, Vorzeichen, Exponenten oder Rundung; maximal `21474836,47`. Ein teurerer Artikel als 1,50 € darf im Sortiment stehen, ist aber nicht aus diesem Tagesbudget kaufbar.

## SQL-Reihenfolge und Wiederherstellung

| Modus | Aufbau | Rückkehr zum Ausgangszustand |
|---|---|---|
| Nutzung | `src/main/resources/db/schema.sql`; kein Seed | Gleichen Bestand wieder öffnen; kein Reset im Programm |
| Demo | Schema, dann `src/main/resources/db/demo.sql` | `ende`, neuen Demo-Prozess starten |
| Test | Schema, dann `src/test/resources/db/testdaten.sql` je frischer H2-Instanz; Dateitest im JUnit-Tempverzeichnis | Prüfungsbefehl erneut ausführen, keine Nutzdatei berühren |

Kein Seed ist für wiederholtes Einspielen in dieselbe Datenbank gedacht. Die reproduzierbare Einheit ist **neue leere Datenbank, Schema, genau ein Seed**. H2 vergibt die IDs ab 1; beide Seeds enthalten fünf Kinder, fünf Artikel und fünf Käufe. Die laufende Anwendung enthält das Testskript nicht.

## Häufige Fehler

- **`release version 25 not supported`:** `sh ./mvnw -v` prüfen, nicht nur `java -version`. Meist verwendet Maven ein anderes `JAVA_HOME`.
- **Wrapper-Download oder TLS-Fehler:** Netzwerk und vertrauenswürdige Zertifikate für JDK und System prüfen. In Hochschulnetzen ggf. den vorgesehenen Proxy konfigurieren, TLS-Prüfung nicht abschalten.
- **JAR fehlt:** Im Verzeichnis der `pom.xml` zuerst `clean verify` vollständig abschließen. `test` allein erzeugt kein JAR.
- **Datei gesperrt / keine Rechte:** Nur eine Sitzung pro Lager, beschreibbarer Projektordner. Diagnose erscheint auf stderr. Keine Sperrdatei und keine Nutzdatei löschen. Bei unklarem Commit-Ausgang erst `auszug|ID` ansehen, nicht blind erneut buchen.
- **Falsche Zeichen:** UTF-8-Terminal verwenden; unter Linux etwa `export LC_ALL=C.UTF-8`, unter Windows UTF-8 in der Terminal-/IDE-Konsole einstellen.
- **Unerwartetes Tageslimit oder leere Liste:** Prüfen, ob Modus, Lagerdatum, Arbeitsverzeichnis und Kind-ID stimmen. Im Demo-Modus ist der heutige Saft bei ID 1 absichtlich schon gebucht.

## Inhaltsverzeichnis des Pakets

| Ort | Inhalt |
|---|---|
| `src/main/java/kiosk/` | `Main`, `Menue`, `Regeln`, `Datenbank`, `Jdbc`, `Stammdaten`, `KaufService`, `Abrechnung` |
| `src/main/resources/db/`, `src/test/resources/db/` | Schema und strikt getrennte Seeds |
| `src/test/java/kiosk/` | Fachtests, H2-Integrationstests und instrumentierte Messung |
| `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/`, `.github/workflows/build.yml`, `.gitignore` | Versionierter Build, Wrapper und CI-Konfiguration |
| `demo/eingaben.txt` | Wiederholbare Konsolenvorführung |
| `docs/01_Projektsteckbrief`, `02_Projektdokumentation`, `03_Arbeitsplan`, `04_Testplan` | Aktuelle Fassungen jeweils als bearbeitbares DOCX und als PDF |
| `docs/Datenmodell.mmd`, `docs/Anforderungsmatrix.md` | ER-Modell und alle 15 Bewertungskriterien |
| `docs/Qualitaet.md`, `nachweise/` | Messung, Refactoring-Diff, Prüfprotokolle und kritischer Zweitreview |
| `docs/Praesentation.md`, `docs/Quellen-und-KI.md` | Vorführskript, Erklärfragen, Herkunft und Grenzen |
