# Qualitätsnachweis | Was tatsächlich geprüft wurde

Stand 1.0.0, lokale Prüfung vom 19.11.2026. Umgebung: Ubuntu 26.04.1 LTS x86_64, Zulu OpenJDK 25.0.2, Maven 3.9.9, H2 2.3.232, JUnit Jupiter 5.11.4 und Surefire 3.5.4. Keine Zeitmessung und kein externer CI-Beleg. Die Dateien in `nachweise/` sind Auszüge echter Ausführungen.

## Messfrage: Wächst die Anzahl der SELECTs für die Abschlussliste?

Gemessen wird ein Aufruf von `Abrechnung.alle()`: die Zahl von H2 tatsächlich gestarteter SELECT-Statements im Aufrufthread, nicht die Zahl gelesener Zeilen, Verbindungsöffnungen, physischer Plattenzugriffe oder Millisekunden. Der H2-`DatabaseEventListener` erfasst `STATE_STATEMENT_START`. Das Messfenster wird erst nach dem Datenaufbau geöffnet und im `finally` geschlossen. Keine Änderung am Produktivcode für die Messung.

Für jede Größe beginnt `MessungTest` mit einer neuen H2-Instanz, dem Schema und dem Test-Seed mit fünf Käufen für fünf Kinder. Jedes zusätzliche Kind kauft am simulierten Tag 3 genau einen Saft zu 100 Cent über den regulären Kaufservice. Somit gelten alle Limits. Die Methode prüft unabhängig vom Zähler die Zahl zurückgegebener Kinder und die Summe `325 + (Anzahl − 5) × 100` Cent. Pro Ausführung gibt es einen Aufruf pro Größe. Warm-up und Mittelwerte sind für diesen diskreten Statement-Zähler nicht erforderlich.

| Kinder | Käufe | geprüfte Gesamtausgaben | beobachtete SELECTs |
|---:|---:|---:|---:|
| 5 | 5 | 325 Cent | 1 |
| 50 | 50 | 4.825 Cent | 1 |
| 500 | 500 | 49.825 Cent | 1 |

Reproduktion: `sh ./mvnw -B -ntp "-Dtest=MessungTest" test`. Die Abschlussliste nutzt eine gruppierte Abfrage mit `LEFT JOIN`, keine zusätzliche Abfrage pro Kind. Das erklärt den konstanten Zähler in diesen drei Beständen; es bedeutet weder konstante Laufzeit noch konstanten Speicherbedarf. Die Liste und die Konsolenausgabe wachsen mit der Anzahl der Kinder. Für die Messung wird nur die Liste erzeugt, nicht gedruckt.

## Refactoring: Generierte IDs nur einmal behandeln

Im neu entwickelten und bereits lauffähigen Ausgangsstand besaßen `neuesKind` und `neuerArtikel` jeweils eigene Blöcke für `RETURN_GENERATED_KEYS`, Zeilenzahlprüfung und Schließen des Schlüssel-ResultSets. Eine künftige Fehlerkorrektur hätte beide Stellen ändern müssen. Nach dem grünen Ausgangsbuild wurde diese Datei gesichert. Erst danach erhielt `Stammdaten` die private Methode `anlegen(sql, werte)`: Sie bündelt den technischen Vertrag, während Validierung und die zwei konkreten INSERT-Anweisungen bei den Fachmethoden bleiben. Keine Änderung an Fachregeln, Schema oder Testmethoden zwischen diesen beiden Läufen.

Der vollständige echte Unified Diff steht in `nachweise/refactoring.diff` (alter Stand mit `-`, neuer mit `+`). `nachweise/build-vorher.txt` und `build-nachher.txt` zeigen je 24 erfolgreich ausgeführte Tests, darunter `stammdatenUndSuche`, `artikelCrud` und `ungueltigeStammdatenOhneSchreiben`. Das sind lokale Dateistände, keine historischen Team-Commits. Zur Nachprüfung kann man in einer **Wegwerfkopie** mit `git apply -R nachweise/refactoring.diff` die Ausgangsfassung rekonstruieren und dieselben Tests ausführen.

## Warum der Rollback-Test eine Teilbuchung erkennen kann

Die Ausgangslage von Kind 5 ist unabhängig abgefragt: kein Kauf, 1.500 Cent Rest. Eine zusätzliche H2-CHECK-Regel nur in dieser Testdatenbank erlaubt für dieses Kind weiterhin nur den ursprünglichen Budgetwert. Sie behindert das Kauf-INSERT nicht. Der produktive Ablauf prüft genau eine eingefügte Zeile und versucht anschließend, das Budget auf 1.400 zu setzen. Erst dort meldet H2 SQLState `23513` und den Namen `TEST_UPDATE_STOP`. Der Test verlangt beides. Eine frühe fachliche Ablehnung oder ein anderer SQL-Fehler genügt nicht. Danach fragt der Test über neue Verbindungen beide Werte erneut ab: 0 Käufe, 1.500 Cent.

**Gegenprobe:** Nur in einer isolierten Wegwerfkopie wurde direkt nach dem INSERT ein zusätzliches `c.commit()` eingefügt. `mvn -B -ntp "-Dtest=KioskDbTest#rollbackNachInsert" test` schlug erwartungsgemäß fehl: `expected: <0> but was: <1>`. Ein Test, der lediglich die Exception prüft, hätte diesen Fehler übersehen. Der defekte Code ist nicht im Projekt; siehe `nachweise/mutationsprobe.txt`. Der Test weist Atomarität des Zustands nach; er unterscheidet bewusst nicht zwischen einem expliziten und einem beim Verbindungsschluss impliziten Rollback. Der explizite Aufruf und die Behandlung eines sekundären Rollback-Fehlers wurden zusätzlich im Code geprüft.

## Kritischer Zweitreview

| Prüfungsfrage | Befund und Konsequenz |
|---|---|
| Könnte ein Test schon vor dem ersten Schreiben grün werden? | Spezifischer SQLState und Constraint-Name plus Mutationsprobe schließen die triviale Frühablehnung aus. |
| Hat die Fachlogik versteckte Uhr- oder DB-Abhängigkeiten? | Ein `Clock`-Zeitpunkt je Kauf. Lokale Grenzen werden mit der Berliner Zone berechnet; Mitternacht mit Nanosekunde vor/nach Grenzwechsel getestet. Beide Writes bekommen dieselbe Connection. |
| Stimmt die Erwartung bei 1.450 Cent? | Zehn Tage erlauben höchstens 1.500 Cent. Erst 9 × 150, dann am letzten Tag 100; 51 abweisen, 50 erlauben. Nur der isolierte Schutztest simuliert einen zu kleinen Rest unabhängig von der Tageshistorie. |
| Haben alte Daten beim nächsten Start Bestand? | Eigene temporäre H2-Datei im Test. Verbindungen vollständig schließen, erneut initialisieren, Kauf und Budget mit neuer Instanz lesen. Regulärer Start führt niemals den Seed oder ein DROP aus. |
| Werden Berichte vor der Prüfung ausgegeben? | `alle()` erzeugt und prüft die gesamte Ergebnisliste vor dem Drucken. Eine Differenz zwischen Startbudget minus Historie und gespeichertem Rest führt zu Diagnose statt Ausgabe einer ungesicherten Gesamtsumme. |
| Schließt der Code auch auf Fehlerpfaden? | Erwerb und Freigabe von Connections, Statements, Schlüsseln und ResultSets im Code gegengeprüft. Try-with-resources, kein Pool, kein durch DAOs versteckt eröffneter Schreibkontext. |
| Zeigt die Demo andere Geschäftsregeln? | Nur Uhr und Datenquelle wechseln; `KaufService` und `Abrechnung` bleiben identisch. Wiederholte Demo-Sitzungen starten beim gleichen Seed. |
| Reicht ein grüner lokaler Build? | Nein. Externer CI-Lauf des echten Abschlusscommits, Teamverständnis und formale Einreichung sind nicht simuliert. |

Die Erstinstallation der Werkzeuge traf auf die lokale TLS-Prüfung der Sandbox. Die vorhandenen vertrauenswürdigen Sandbox-CA-Zertifikate wurden nur in das temporäre JDK importiert, ohne TLS-Prüfungen abzuschalten. Keines dieser Zertifikate und kein Cache sind Teil des Pakets. Auf normalen Rechnern sind keine Sandbox-Einstellungen nötig.

## Paketprüfung außerhalb des Entwicklungsverzeichnisses

Ein ZIP-Kandidat wurde in ein leeres Verzeichnis entpackt. Wrapper und Maven bekamen zunächst leere Download- bzw. Artefaktverzeichnisse. Die README-Befehle `sh ./mvnw -v`, `sh ./mvnw -B -ntp clean verify` und der Start des erzeugten JARs genügten. Beim Gegenlesen war ein Aufruf ohne `./` als fehlerhaft aufgefallen; Anleitung und Workflow enthalten die getestete Schreibweise. Der Wrapper bezieht die festgelegte Maven-Distribution mit SHA-256-Prüfung.

Die drei Surefire-XML-Berichte wurden anhand ihrer Zähler kontrolliert; die Konsolendemo lief zweimal in separaten JVMs mit identischer Ausgabe. An der echten CLI wurden auch Kaufabbruch, ungültige Eingaben, Löschschutz, Neustart der Nutzdatei, unbrauchbarer Datenpfad und ungültiges Startdatum geprüft. Der zusätzliche Konsolentest zählt nicht zu den 24 JUnit-Methoden. Gestraffte Originalausgaben: `nachweise/kaltstart-build.txt`, `nachweise/paketpruefung.txt`, `nachweise/demo.txt`. Prüfplattform war Linux; ein Windows- oder IDE-Lauf des Teams wird nicht als ausgeführt behauptet.
