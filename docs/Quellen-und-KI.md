# Quellen, Ausgangslage und KI-Unterstützung

## Verbindliche Grundlage

04_AufgabeTestat-1.pdf, Aufgabe Testat Fortgeschrittene Programmierung, Aufgabenblatt V4, fünf Seiten. Am 24.11.2026 direkt gelesen: technischer Rahmen und Mindestumfang auf Seite 1, Qualitätsanforderungen auf Seite 2, Zusammenarbeit/Abgabe auf Seite 3 und Kriterien 1–15 auf Seiten 4–5.

Das Blatt verlangt Java 25, eine relationale Datenbank mit JDBC, reproduzierbare Tests und einen automatischen GitHub-Actions-Lauf der Abschlussversion. Ein separates Testplan-Dokument oder eine Präsentationsdatei ist nicht ausdrücklich vorgeschrieben; Normal-/Fehlerfälle und Demo-Schritte müssen trotzdem dokumentiert sein. Umsetzung: Anforderungsmatrix.md und README.

Abweichende Teamgröße ist abzustimmen. Daniel Wiebe und Jonas Kölker sind als Team bestätigt; eine Genehmigung der Zweiergruppe wird hier nicht behauptet. Präsentation laut Projektauftrag: 25.11.2026. Offizieller Abgabetermin und tatsächlicher Lagerbeginn sind nicht bestätigt.

## Ergänzende Kursunterlagen und historische Verweise

Die frühere Dokumentation nennt 01_Fortgeschrittene_Programmierung_V06-1.pdf, 02_Versionsverwaltungssysteme-2.pdf und 03_Einfuehrung-in-Git-2022-1.pdf. Die damaligen Detail- und Folienverweise wurden in dieser Abschlusskorrektur nicht vollständig erneut geprüft. Eine zusätzlich verfügbare Datei 01_Fortgeschrittene_Programmierung-1.pdf ist nicht ohne Abgleich mit der früher genannten Fassung gleichzusetzen.

Das separate VCS-Praktikum sowie individueller Java-Feature-Vortrag und Kurztests sind von diesem gemeinsamen Programmierprojekt zu unterscheiden. Frühere Angaben zu ungefähr 15 VCS-Commits sind keine zusätzliche Vorgabe aus dem hier direkt geprüften Programmier-Aufgabenblatt. Keine künstliche oder rückdatierte Historie erzeugen. Individuelle Leistungen, Themenanmeldung und Präsentationsdauer der Gruppe sind hier nicht belegt.

## Technische Referenzen und Fremdkomponenten

- Oracle Java SE 25, java.sql.Connection: https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html
- Apache Maven Wrapper: https://maven.apache.org/wrapper/
- Mitgelieferte Wrapper-Skripte: Version 3.3.2 mit Apache-Lizenzhinweisen; diese bleiben unverändert erhalten.
- Maven 3.9.9 und SHA-256-Prüfung der Distribution: .mvn/wrapper/maven-wrapper.properties.
- H2 2.3.232 und JUnit Jupiter 5.11.4: pom.xml.

Die Links benennen technische Referenzen; in dieser Dokumentkorrektur wird keine erneute vollständige Prüfung dieser externen Seiten behauptet.

## Ausgangslage und Dokumentfassungen

Die historische Projektdokumentation beschreibt eine KI-gestützte Neuimplementierung auf Basis bereitgestellter Unterlagen, nicht die Übernahme einer belegten früheren menschlichen Commit-Historie. Frühere Entwürfe und Vorschläge für Personen A/B/C sind keine tatsächlichen Teambeiträge.

Die früher dokumentierte Sichtung von vier DOCX-/PDF-Entwurfspaaren ist ein historischer Bericht. Sie bestätigt nicht die Übereinstimmung der derzeit auf GitHub liegenden Dateien, insbesondere nicht des ersetzten Arbeitsplans. Die aktuell verfügbaren älteren Anhänge sind nicht allein wegen gleicher Dateinamen mit den Remote-Dateien gleichzusetzen.

Die Testplan-Dateien und die interne Präsentationsanleitung wurden am 19.11.2026 auf ausdrücklichen Wunsch des Nutzers vom Arbeitsbranch entfernt. Automatisierte Tests, Fallzuordnung und technische Qualitätsnachweise bleiben erhalten.

## Tatsächliche KI-Unterstützung

Laut vorhandenem Qualitätsnachweis wurde Perplexity Computer für Sichtung, Neuimplementierung der Java-/SQL-Dateien, Tests, lokale Build-/Demoläufe, Messung, Refactoring, Mutationsprobe und Dokumentationsentwurf eingesetzt. Die Protokolle sind vorhandene historische Nachweise, keine in dieser Unterhaltung neu ausgeführten Versuche.

In dieser Unterhaltung unterstützte der Assistent bei Git-Kommandos, las Repository-Dateien und Check-Ergebnisse und führte nach ausdrücklicher Bestätigung Dokumentationscommits und Dateilöschungen auf daniel/dokumente-abschluss aus. Die weitere Abschlusskorrektur bereinigt Verweise und Statusangaben und ergänzt geforderte Demo-/Fallbeschreibungen. Eine Lesekontrolle ist kein menschliches Peer-Review und kein neuer lokaler Testlauf.

## Repository- und CI-Nachweis

Repository: https://github.com/danilwie/Sommerkiosk

PR #1: https://github.com/danilwie/Sommerkiosk/pull/1

Für Branch-Stand 2f7a86f8bbaf44bd1ff3a59b54582a494825fbc4 wurde der verify-Check als erfolgreich abgerufen:
https://github.com/danilwie/Sommerkiosk/actions/runs/44575185830

Damit ist die frühere pauschale Aussage, Repository/Remote-Lauf seien nicht nachgewiesen, überholt. Ein Nachweis für den noch festzulegenden finalen Abgabecommit entsteht dadurch nicht automatisch. Beim Abruf am 24.11.2026 war PR #1 offen, GitHub meldete mergeable_state dirty und die Reviewliste war leer. Es wird weder eine Freigabe noch ein Merge behauptet.

Vor Abgabe KI-Nutzung nach Kursregeln offenlegen, echte eigene Beiträge/Prüfungen ergänzen und alle Programmteile selbst erläutern können. Keine fremden Ergebnisse als persönliche Leistung ausgeben.
