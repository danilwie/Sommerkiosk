# Anforderungs- und Nachweismatrix

Stand: 27.11.2026. Grundlage: 04_AufgabeTestat-1.pdf, Aufgabenblatt V4, Bewertungskriterien 1–15. AF1 = Stammdaten, AF2 = Kauf, AF3 = Abrechnung.

Testergebnisse, CI-Läufe und der lokale Lauf auf einem Teamrechner stehen in Pruefstand.md.

| Nr. / Anforderung | Implementierung und konkreter Nachweis | Einordnung / Grenze |
|---|---|---|
| 1: Mindestens drei zusammenhängende Abläufe; je Normal- und Fehler-/Grenzfall | AF1 Stammdaten, AF2 KaufService, AF3 Abrechnung; verbunden im Menue. Fälle siehe unten; Demo in README. | Demo am 26.11.2026 auf einem Teamrechner ausgeführt (Pruefstand.md). |
| 2: Mindestens drei Tabellen mit PK und zwei wirksamen FK; Modell stimmt | schema.sql: kind, artikel, kauf; Kauf referenziert Kind und Artikel. Datenmodell.mmd; wirksameFremdschluessel und wiederaufbau. | Schema und Modell stimmen überein; wiederaufbau baut das Schema aus den Skripten neu auf. |
| 3: CRUD insgesamt, parametrisierte Suche | Stammdaten: anlegen, lesen, Artikel ändern/löschen; LOCATE(LOWER(?),LOWER(name)). stammdatenUndSuche, artikelCrud, wirksameFremdschluessel. | Bindung über PreparedStatements; Löschen nur unreferenzierter Artikel. |
| 4: Mindestens zwei zusammengehörige Schreibschritte; alles oder nichts | KaufService.buchen: Kauf-INSERT und Budget-UPDATE auf derselben Connection; Commit/Rollback. commit und rollbackNachInsert. | Rollback-Test prüft Datenzustand über separate Connections. |
| 5: Ungültige Eingaben, unbekannte Datensätze, Verbindungsfehler | Regeln, Ablehnung, Jdbc.eins, Menue, Main.technik; falschePreise, unbekanntUndGesperrt, verbindungFehlt. | Fachliche Ablehnung und SQLException getrennt. |
| 6: Ressourcen zuverlässig schließen | try-with-resources in JDBC-Helfern, Datenbank, Services und Main; rollbackNachInsert, dateiNeustart. | Durch Codedurchsicht geprüft; ein Langzeit-Leaktest gehört nicht zu den Tests. |
| 7: Bedienung, Fachlogik, Datenzugriff trennen; Verträge | Menue, Regeln, Services und Jdbc; Main verdrahtet. Projektdokumentation und README. | Verträge und Annahmen in Projektdokumentation und README. |
| 8: Reproduzierbare Skripte; mindestens fünf Datensätze je Tabelle | Schema zuerst, danach genau ein Seed. demo.sql und testdaten.sql je 5/5/5; wiederaufbau prüft fünf Käufe und 325 Cent Testausgaben. | Reihenfolge und Trennung stehen in README. |
| 9: Mindestens zwölf unterschiedliche automatisierte Fälle | RegelnTest: 5, KioskDbTest: 18, MessungTest: 1. Aussagekräftige Assertions einschließlich Grenzfällen und Zustandskontrollen. | 24 Tests, zuletzt lokal am 26.11.2026 und in GitHub Actions bestanden. |
| 10: Mindestens sechs echte DB-Tests mit Commit und Rollback | KioskDbTest nutzt H2; commit, rollbackNachInsert, artikelCrud, historischePreise, dateiNeustart und weitere. | 18 Methoden in DB-Suite; eine davon prüft gezielt einen Verbindungsfehler. |
| 11: Unabhängig wiederholbar; dasselbe DB-System | UUID-In-Memory-DB je Test, Schema/Seed in BeforeEach, TempDir für Dateitest. H2-Version zentral in POM. | Test- und Nutzdaten getrennt; kein persönlicher Datenbestand erforderlich. |
| 12: Automatisierter Build/Test der Abschlussversion | .github/workflows/build.yml: clean verify; zusätzliche Prüfung auf drei Surefire-Berichte. Schema/Seed durch Tests. | Erfolgreiche Läufe in Pruefstand.md; der Abgabecommit erhält einen eigenen Lauf. |
| 13: Reproduzierbare Messung | MessungTest.selectAnzahlAbschluss: 5/50/500 Kinder, jeweils eine SELECT-Ausführung. Qualitaet.md, build-nachher.txt. | Aussage über Statement-Anzahl, nicht konstante Laufzeit. |
| 14: Begründetes Refactoring mit Vorher/Nachher und Tests | Stammdaten.anlegen bündelt ID-Behandlung; nachweise/refactoring.diff und build-vorher.txt/build-nachher.txt. | Vorher- und Nachher-Build mit denselben 24 Tests. |
| 15: Reproduzierbarer, eindeutig bezeichneter Abgabestand | README, POM/Wrapper, src, Schema/Seeds, Datenmodell, Steckbrief, Projektdokumentation, Arbeitsplan, Matrix, Quellen/KI und Qualitätsnachweise; Prüfsummen in nachweise/SHA256SUMS.txt. | Abgabe als ZIP des Abgabecommits; Commit-ID und CI-Lauf werden bei der Abgabe genannt. |

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

## Grenzen

Das Aufgabenblatt sieht Dreierteams vor; die Bearbeitung als Zweierteam ist nach Angabe des Teams mit dem Professor abgestimmt. Ein realer Lagerbeginn ist nicht festgelegt; Demo und Tests verwenden feste Beispielzeiträume. Storno, Auszahlung, GUI, Bestandsmengen, Mehrplatzbetrieb und Migration sind nicht implementiert.
