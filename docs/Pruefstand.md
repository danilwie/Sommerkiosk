# Prüfstand vom 19.11.2026

## Bezug und Grenzen

Die hier beschriebene Lesekontrolle bezieht sich auf Branch daniel/dokumente-abschluss, Commit 178773bfac644d38f0aa07705002878c7b63009c vor dieser Dokumentkorrektur. Sie ist keine vollständige Abnahme, keine neue lokale Testausführung und kein menschliches Teamreview.

## Tatsächlich geprüft

- Alle acht Java-Hauptklassen gelesen: Abrechnung, Datenbank, Jdbc, KaufService, Main, Menue, Regeln, Stammdaten.
- Alle drei Testklassen gelesen: KioskDbTest (18 Testmethoden), RegelnTest (5), MessungTest (1). Die Methodenanzahl ist eine Quelltextzählung, kein neu ausgeführter Testbericht.
- schema.sql, demo.sql und testdaten.sql gelesen. Das Schema hat drei Tabellen mit Primärschlüsseln und zwei Fremdschlüsseln. Beide Seeds enthalten je fünf Kinder, Artikel und Käufe.
- Datenmodell.mmd mit den gelesenen Tabellen und Beziehungen abgeglichen.
- README, POM, CI-Workflow, Anforderungsmatrix, Quellen-und-KI, Qualitaet und Praesentation gelesen; teilweise in den unmittelbar vorhergehenden Prüfschritten derselben Unterhaltung.
- PR-Dateiliste geprüft: README und Arbeitsplan-DOCX geändert, Arbeitsplan-PDF nicht geändert.
- GitHub-Check-Ergebnisse geprüft: beide verify-Checks erfolgreich; PR-Lauf nach Wiederholung.

## Beobachtungen aus dem Code

- Kauf-INSERT und Budget-UPDATE nutzen dieselbe Connection mit deaktiviertem Auto-Commit. Commit erfolgt nach beiden geprüften Schreibschritten; SQL- und Laufzeitfehler führen zum Rollback-Versuch.
- SQL-Nutzwerte werden über PreparedStatements gebunden. Ressourcen werden an den gelesenen Erzeugungsstellen mit try-with-resources geschlossen.
- Tagesgrenzen werden aus Europe/Berlin berechnet; Kaufpreise werden historisch gespeichert.
- Der Rollback-Test erzwingt eine benannte CHECK-Verletzung beim zweiten Schreibschritt und prüft anschließend Kaufzahl und Restbudget über separate Connections.
- MessungTest prüft die SELECT-Anzahl für die Abschlussliste, nicht deren Laufzeit.
- Kein aus dieser Lesekontrolle belegter Anlass, die Java- oder SQL-Dateien vor dem Dokumentabgleich auf Verdacht zu verändern. Dies ist keine Fehlerfreiheitsgarantie.

## Verifizierter Remote-Status

Push-Lauf: https://github.com/danilwie/Sommerkiosk/actions/runs/44569977906

PR-Lauf: https://github.com/danilwie/Sommerkiosk/actions/runs/44570965319

Der erste PR-Versuch scheiterte beim Maven-Download. Der erneut ausgeführte verify-Job war erfolgreich. Diese Ergebnisse beziehen sich auf den oben genannten Stand, nicht automatisch auf spätere Änderungen.

## Einordnung älterer Statusangaben

Anforderungsmatrix.md und Quellen-und-KI.md enthalten noch historische Aussagen über nicht belegte Repository-/CI-Arbeit. Inzwischen sind der gepushte Branch, PR #1 und die oben genannten erfolgreichen Checks nachgewiesen. Historische lokale Prüfungen sind dadurch weder neu ausgeführt noch einzelnen Teammitgliedern zugeordnet. Die alten Texte müssen noch im Zusammenhang überarbeitet werden.

Qualitaet.md beschreibt einen früheren lokalen Prüfstand. Die dortige Formulierung ohne externen CI-Beleg gilt für diesen historischen Nachweis, nicht als aktueller Gesamtstatus des Repositorys. Die enthaltenen lokalen Protokolle und die Mutationsprobe wurden in dieser Kontrolle nicht vollständig neu geprüft oder reproduziert.

## Noch offen

1. Neuen Arbeitsplan-DOCX-Text mit der vorhandenen PDF vergleichen; über die GitHub-Schnittstelle wurde bisher nur die Binärdatei bestätigt. Gleichheit wird nicht behauptet. Bei Abweichungen PDF aus der aktuellen DOCX neu exportieren.
2. Auch die anderen DOCX-/PDF-Paare inhaltlich lesen und abgleichen.
3. Originalaufgabenblatt direkt prüfen. Die Matrix verweist auf Dreierteam oder abgesprochene Abweichung; die Genehmigung des Zweierteams ist hier nicht belegt.
4. Praesentation.md korrigieren: Die beiden Aussagen Alle drei Personen und Alle drei passen nicht zum bestätigten Team Daniel Wiebe und Jonas Kölker. Verweise auf Demo-Befehle in README sind ebenfalls abzugleichen.
5. Anforderungsmatrix.md und Quellen-und-KI.md im Zusammenhang auf den verifizierten Remote-Status aktualisieren, ohne historische Prüfnachweise zu überschreiben oder neue Teambeiträge zu erfinden.
6. Alle Dateien in nachweise/ sowie Wrapper-Konfiguration, Wrapper-Skripte, .gitignore und Demo-Eingaben vollständig prüfen.
7. Echte Beiträge und menschliche Prüfungen von Daniel und Jonas nur nach deren Bestätigung dokumentieren.
8. Nach Dokumentkorrekturen neue CI-Ergebnisse prüfen.
9. Jonas prüft PR #1 tatsächlich; danach über Zusammenführung entscheiden. Keine Freigabe wird vorweggenommen.
10. Präsentation auf den persönlichen Rechnern proben und formale Einreichung sowie Termin klären.

## Änderungen dieses Dokumentationsschritts

- README.md: maskierte Markdown-Zeichen, Tabellenabstände und Windows-Wrapper-Aufruf korrigiert; bestätigte CI-Ergebnisse mit ihrem Bezugsstand dokumentiert.
- docs/Pruefstand.md: Lesekontrolle, Grenzen, verifizierten Remote-Status und offene Aufgaben dokumentiert.
- Keine Java-/SQL-Änderungen, keine neuen lokalen Tests, keine DOCX-/PDF-Änderungen und kein Merge in diesem Schritt.
