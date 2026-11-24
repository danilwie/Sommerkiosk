# Prüfstand und Freigabegrenzen

Stand: 24.11.2026. Bezug des Repository-Abrufs vor dieser Korrektur: Branch daniel/dokumente-abschluss, Commit 2f7a86f8bbaf44bd1ff3a59b54582a494825fbc4. Kein endgültiger Abgabe- oder Freigabevermerk.

## Tatsächlich ausgeführte Kontrollen

- In der vorherigen Lesekontrolle alle acht Java-Hauptklassen, drei Testklassen und drei SQL-Dateien gelesen; Schema und Datenmodell abgeglichen. Der aktuell abgerufene src-Baum ist gegenüber dem zuvor geprüften Stand unverändert.
- README und Dokumentliste erneut gelesen; die drei gewünschten Löschungen sind weiterhin vorhanden, das heißt die betreffenden Dateien fehlen im Branch.
- Originalaufgabenblatt V4 direkt gelesen und die Dokumentationsanforderungen gegengeprüft.
- Anforderungsmatrix und Quellen/KI erneut gelesen; Qualitaet.md war Bestandteil der vorherigen Lesekontrolle.
- Alle acht Dateien aus nachweise gelesen: build-vorher.txt, build-nachher.txt, kaltstart-build.txt, demo.txt, mutationsprobe.txt, paketpruefung.txt, refactoring.diff, SHA256SUMS.txt.
- .gitignore, Demo-Eingaben, Wrapper-Konfiguration sowie mvnw und mvnw.cmd gelesen. Die Skripte wurden nicht neu ausgeführt. Wrapper-Lizenzhinweise bleiben erhalten.
- PR-Metadaten, aktuelle Check-Ergebnisse und Reviewliste abgerufen. README auf main zur Einordnung des Konfliktstatus gelesen; keine Konfliktlösung oder Änderung auf main vorgenommen.

## Nachweise und ihre Aussage

build-vorher, build-nachher und kaltstart-build melden jeweils 24 Tests ohne Fehler oder übersprungene Tests. mutationsprobe enthält den erwarteten Fehlschlag der isolierten Gegenprobe. Der Refactoring-Diff zeigt die Bündelung der generierten ID-Behandlung in Stammdaten.anlegen. Das Demoprotokoll enthält 13,50 Euro Einzelrest und 70,75 Euro Gesamtbetrag nach den dokumentierten Eingaben. Lesen dieser Dateien ist keine erneute Ausführung oder unabhängige Bestätigung ihrer gesamten Entstehungsgeschichte.

Der abgerufene verify-Check zum Bezugscommit ist erfolgreich:
https://github.com/danilwie/Sommerkiosk/actions/runs/44575185830

Neue Dokumentationscommits benötigen einen eigenen erfolgreichen Lauf. Kein lokaler Maven-/Java-Lauf wurde in dieser Abschlusskorrektur ausgeführt. Der vorhandene SHA256SUMS-Bericht ist historisch: Er enthält inzwischen entfernte Dateien und ältere Fassungen. Er wurde nicht als aktuelles Manifest bestätigt oder mit erfundenen Werten erneuert.

## Änderungen dieser Abschlusskorrektur

- README.md: tote Dokumentverweise entfernt, Einrichtung und Demo der drei Abläufe konkretisiert, SQL-Reihenfolge und aktueller belegter CI-Bezug dokumentiert.
- docs/Anforderungsmatrix.md: Originalblatt-Abgleich, Zuordnung aller 15 Kriterien, Normal-/Fehlerfälle und zutreffender Remote-Status.
- docs/Quellen-und-KI.md: historische und aktuelle Nachweise getrennt; Löschungen, KI-Anteile und belegte Remote-Arbeit eingeordnet.
- docs/Pruefstand.md: Prüfungen, Grenzen und Freigabehindernisse aktualisiert.
- Keine Java-/SQL-Änderung, keine neuen lokalen Tests, keine DOCX-/PDF-Änderung, keine Wiederherstellung der gelöschten Dateien und kein Merge.

## Vor endgültiger Freigabe offen

1. GitHub meldet PR #1 als offen und mergeable_state dirty. Konfliktursache im aktuellen Vergleich feststellen und bewusst lösen; kein Force-Push und kein blindes Überschreiben von main.
2. Neue CI zum Dokumentationscommit prüfen; nach Konfliktlösung auch den tatsächlich zur Abgabe vorgesehenen Stand prüfen.
3. In der abgerufenen Reviewliste steht kein Review. Tatsächliche Prüfung durch Jonas und Teamfreigabe nicht vorwegnehmen.
4. Aktuelle Remote-DOCX-/PDF-Inhalte vollständig vergleichen. Ältere lesbare Anhänge sind nicht sicher dieselbe Fassung. Insbesondere wurde die Arbeitsplan-DOCX ersetzt, die PDF im damaligen PR nicht mitgeändert.
5. Steckbrief/Projektdokumentation können ältere offene CI-Statusangaben enthalten; Abgleich mit ihren aktuellen Remote-Fassungen und gegebenenfalls Aktualisierung erforderlich.
6. Echte Beiträge von Daniel/Jonas bestätigen, Zweierteam-Abstimmung und offiziellen Abgabeweg/Termin klären. Keine Bestätigung erfinden.
7. Auf dem persönlichen Vorführrechner README-Build und Demo proben. Beide müssen alle Abläufe und die Transaktion erklären können.
8. Nach Freigabe eindeutigen Abgabecommit festhalten und genau diesen Stand einreichen. Eine Veröffentlichung oder Zusammenführung erfolgt nur nach gesonderter Zustimmung.
