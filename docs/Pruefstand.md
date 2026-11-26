# Prüfstand

Stand: 27.11.2026. Java-Code, Tests, SQL-Skripte und pom.xml sind seit dem Importcommit 91699aa unverändert. Alle späteren Commits betreffen Dokumentation und Nachweise.

## GitHub Actions

Workflow: .github/workflows/build.yml. Er läuft bei Push und Pull Request, richtet JDK 25.0.2 (Zulu) ein, führt `sh ./mvnw -B -ntp clean verify` aus und prüft, dass die Berichte aller drei Testklassen vorliegen.

| Commit | Lauf | Ergebnis |
|---|---|---|
| 91699aa (Import) | https://github.com/danilwie/Sommerkiosk/actions/runs/44561539453 | erfolgreich |
| 2f7a86f | https://github.com/danilwie/Sommerkiosk/actions/runs/44575185830 | erfolgreich |
| 09b2e49 (Merge von main) | https://github.com/danilwie/Sommerkiosk/actions/runs/45502887248 | erfolgreich |
| 09b2e49 (Merge von main) | https://github.com/danilwie/Sommerkiosk/actions/runs/45502897742 | erster Versuch fehlgeschlagen: Der Maven-Wrapper konnte apache-maven-3.9.9-bin.zip nicht herunterladen, es lief kein Test. Erneuter Lauf am 26.11.2026 erfolgreich. |

Jeder weitere Commit braucht einen eigenen erfolgreichen Lauf. Commit-ID und Lauf des Abgabestands werden bei der Abgabe genannt.

## Lokaler Lauf auf einem Teamrechner

26.11.2026, Rechner von Daniel Wiebe, Windows, OpenJDK 25 (Build 25+36), Daniel Wiebe und Jonas Kölker gemeinsam:

- `.\mvnw.cmd -B -ntp clean verify`: BUILD SUCCESS, 24 Tests (RegelnTest 5, KioskDbTest 18, MessungTest 1), 0 Fehler, 0 übersprungen.
- `Get-Content demo\eingaben.txt | java -jar target\sommerkiosk-1.0.0.jar --demo`: Apfel gebucht (heute 1,50 €, Rest 13,50 €), Wasser wegen Tageslimit abgelehnt, Probeartikel 6 angelegt, geändert und gelöscht, Rückzahlungsbeträge gesamt 70,75 €.

Hinweise aus diesem Lauf: JAVA_HOME war nicht gesetzt und musste für die Sitzung auf das JDK 25 zeigen. Umlaute und € erscheinen in PowerShell erst nach `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8` korrekt. Beides steht in der README.

## Nachweise in nachweise/

| Datei | Inhalt |
|---|---|
| build-vorher.txt, build-nachher.txt | Build vor und nach dem Refactoring, jeweils 24 bestandene Tests |
| kaltstart-build.txt | Build aus einem frischen Verzeichnis |
| mutationsprobe.txt | Gegenprobe: Rollback-Test schlägt bei absichtlich defekter Transaktion fehl |
| refactoring.diff | Vorher-Nachher-Vergleich von Stammdaten.anlegen |
| demo.txt, paketpruefung.txt | Demo-Ausgabe und Prüfung des Pakets |
| SHA256SUMS.txt | Prüfsummen aller Dateien des Abgabestands |

Diese Protokolle stammen aus der KI-gestützten Erstellung (siehe Quellen-und-KI.md). Der lokale Lauf vom 26.11.2026 bestätigt die Testzahl und das Demo-Ergebnis auf einem Teamrechner.

## Bis zur Abgabe

1. Generalprobe beider Teammitglieder: Demo, Transaktion und Rollback-Test, Datenmodell, Abrechnung und Main erklären.
2. Danach PR #1 nach main zusammenführen und den CI-Lauf auf main prüfen.
3. ZIP aus dem Abgabecommit erstellen, entpacken und nach README bauen.
