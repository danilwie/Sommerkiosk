# Anforderungs- und Nachweismatrix

Stand der Dokumentationsprüfung: 24.11.2026. Grundlage: 04_AufgabeTestat-1.pdf, Aufgabenblatt V4, insbesondere Seiten 1–5. Die Vorgaben wurden am Original gegengelesen. AF1 = Stammdaten, AF2 = Kauf, AF3 = Abrechnung. Die Matrix ordnet Implementierung und Nachweise zu; sie ersetzt weder eine Bewertung noch die Freigabe des Teams.

Die Java-/SQL-Quelltexte wurden zuvor gelesen. Der src-Baum ist im aktuell abgerufenen Branch unverändert. Vorhandene lokale Protokolle wurden gelesen, aber nicht neu ausgeführt. Aktuell abgerufener erfolgreicher CI-Check: Stand 2f7a86f8bbaf44bd1ff3a59b54582a494825fbc4, https://github.com/danilwie/Sommerkiosk/actions/runs/44575185830 . Für nachfolgende Änderungen ist ein neuer erfolgreicher Lauf erforderlich.

| Nr. / Anforderung | Implementierung und konkreter Nachweis | Einordnung / Grenze |
|---|---|---|
| 1: Mindestens drei zusammenhängende Abläufe; je Normal- und Fehler-/Grenzfall | AF1 Stammdaten, AF2 KaufService, AF3 Abrechnung; verbunden im Menue. Fälle siehe unten; Demo in README. | Code gelesen; persönlicher Probelauf des Teams nicht bestätigt. |
| 2: Mindestens drei Tabellen mit PK und zwei wirksamen FK; Modell stimmt | schema.sql: kind, artikel, kauf; Kauf referenziert Kind und Artikel. Datenmodell.mmd; wirksameFremdschluessel und wiederaufbau. | Schema/Modell gelesen und abgeglichen. |
| 3: CRUD insgesamt, parametrisierte Suche | Stammdaten: anlegen, lesen, Artikel ändern/löschen; LOCATE(LOWER(?),LOWER(name)). stammdatenUndSuche, artikelCrud, wirksameFremdschluessel. | Bindung über PreparedStatements; Löschen nur unreferenzierter Artikel. |
| 4: Mindestens zwei zusammengehörige Schreibschritte; alles oder nichts | KaufService.buchen: Kauf-INSERT und Budget-UPDATE auf derselben Connection; Commit/Rollback. commit und rollbackNachInsert. | Rollback-Test prüft Datenzustand über separate Connections. |
| 5: Ungültige Eingaben, unbekannte Datensätze, Verbindungsfehler | Regeln, Ablehnung, Jdbc.eins, Menue, Main.technik; falschePreise, unbekanntUndGesperrt, verbindungFehlt. | Fachliche Ablehnung und SQLException getrennt. |
| 6: Ressourcen zuverlässig schließen | try-with-resources in JDBC-Helfern, Datenbank, Services und Main; rollbackNachInsert, dateiNeustart. | Lesekontrolle; kein eigenständiger Langzeit-Leaktest behauptet. |
| 7: Bedienung, Fachlogik, Datenzugriff trennen; Verträge | Menue, Regeln, Services und Jdbc; Main verdrahtet. Projektdokumentation und README. | Jedes Teammitglied muss die gesamte Anwendung erklären können; nicht durch CI bewiesen. |
| 8: Reproduzierbare Skripte; mindestens fünf Datensätze je Tabelle | Schema zuerst, danach genau ein Seed. demo.sql und testdaten.sql je 5/5/5; wiederaufbau prüft fünf Käufe und 325 Cent Testausgaben. | Reihenfolge und Trennung stehen in README. |
| 9: Mindestens zwölf unterschiedliche automatisierte Fälle | RegelnTest: 5, KioskDbTest: 18, MessungTest: 1. Aussagekräftige Assertions einschließlich Grenzfällen und Zustandskontrollen. | 24 Methoden im Quelltext; build-nachher.txt meldet 24 Erfolge. Historischer Bericht, kein neuer lokaler Lauf. |
| 10: Mindestens sechs echte DB-Tests mit Commit und Rollback | KioskDbTest nutzt H2; commit, rollbackNachInsert, artikelCrud, historischePreise, dateiNeustart und weitere. | 18 Methoden in DB-Suite; eine davon prüft gezielt einen Verbindungsfehler. |
| 11: Unabhängig wiederholbar; dasselbe DB-System | UUID-In-Memory-DB je Test, Schema/Seed in BeforeEach, TempDir für Dateitest. H2-Version zentral in POM. | Test- und Nutzdaten getrennt; kein persönlicher Datenbestand erforderlich. |
| 12: Automatisierter Build/Test der Abschlussversion | .github/workflows/build.yml: clean verify; zusätzliche Prüfung auf drei Surefire-Berichte. Schema/Seed durch Tests. | Grüner Remote-Lauf für oben genannten Stand bestätigt. Finaler Abgabecommit noch festzulegen und erneut zu prüfen. |
| 13: Reproduzierbare Messung | MessungTest.selectAnzahlAbschluss: 5/50/500 Kinder, jeweils eine SELECT-Ausführung. Qualitaet.md, build-nachher.txt. | Aussage über Statement-Anzahl, nicht konstante Laufzeit. |
| 14: Begründetes Refactoring mit Vorher/Nachher und Tests | Stammdaten.anlegen bündelt ID-Behandlung; nachweise/refactoring.diff und build-vorher.txt/build-nachher.txt. | Dateien gelesen; keine erfundenen historischen Teamcommits, keine neue Reproduktion. |
| 15: Reproduzierbarer, eindeutig bezeichneter Abgabestand | README, POM/Wrapper, src, Schema/Seeds, Datenmodell, Steckbrief, Matrix, Quellen/KI und Qualitätsnachweise vorhanden. | Dokumentpaar-Abgleich, Konfliktlösung, Teamfreigabe und formale Abgabe noch offen. |

## Normal- und Fehlerfälle

| Ablauf | Ausgangslage | Aktion | Erwartetes Ergebnis / Test |
|---|---|---|---|
| AF1 normal | Frische Testdaten, Artikel Tee noch nicht vorhanden | Anlegen, Preis/Verfügbarkeit ändern, lesen und löschen | Geänderte Werte werden gelesen; nach Löschen wieder fünf Artikel. artikelCrud. |
| AF1 Fehler | Artikel 1 wird durch Käufe referenziert | Artikel 1 löschen | Fachliche Ablehnung, Artikel und fünf Käufe bleiben erhalten. wirksameFremdschluessel. |
| AF2 normal | Kind 5 ohne Käufe, 1.500 Cent; gültiger Lagertag | Artikel 1 zu 100 Cent kaufen | Ein neuer Kauf, 1.400 Cent Rest, heute 100 Cent. commit. |
| AF2 Grenze | Kind 5 hat heute 150 Cent ausgegeben | Weiteren Artikel zu einem Cent kaufen | Ablehnung, weiterhin zwei Käufe und 1.350 Cent Rest. exaktLimitUndEinCentZuviel. |
| AF2 technischer Fehler | Kind 5 ohne Käufe; zusätzliche Test-CHECK-Regel verbietet Budgetabzug | Kauf-INSERT gelingt, Budget-UPDATE scheitert | SQLState 23513 und benannter Constraint; danach null Käufe und 1.500 Cent Rest. rollbackNachInsert. |
| AF3 normal | Kind 5 ohne Käufe; danach Käufe an Tag 1 und Tag 3 | Auszug und Abschlussliste lesen | Zunächst 0 Ausgaben/1.500 Rest; danach 250 insgesamt, 150 heute, 1.250 Rest. abrechnungMitUndOhneKaeufe. |
| AF3 Fehler | Restbudget von Kind 5 passt nicht zur Kaufhistorie | Auszug und Abschlussliste abrufen | SQLException statt Freigabe widersprüchlicher Salden. manipulationInAbrechnungErkannt. |

## Formale Grenzen

Das Blatt nennt ein Dreierteam; abweichende Teamgröße und passender Umfang sind mit der Lehrperson abzustimmen. Bestätigt sind Daniel Wiebe und Jonas Kölker als Projektteam, nicht die Genehmigung der Abweichung. Keine künstlichen Beiträge oder Commits ergänzen.

Ein separates Dokument namens Testplan oder eine Präsentationsdatei ist nicht ausdrücklich gefordert. Die geforderten Fallbeschreibungen bleiben hier, Demo-Schritte in README und Tests in src/test erhalten.

Offen: offizieller Abgabetermin, realer Lagerbeginn, konkrete menschliche Beiträge/Freigabe und Übereinstimmung aktueller DOCX/PDF. Storno, Auszahlung, GUI, Bestandsmengen, Mehrplatzbetrieb und Migration sind nicht implementierte Erweiterungen.
