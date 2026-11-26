# Quellen und KI-Unterstützung

## Grundlage

04_AufgabeTestat-1.pdf, Aufgabe Testat Fortgeschrittene Programmierung, Aufgabenblatt V4 (fünf Seiten): technischer Rahmen, fachlicher Mindestumfang, Qualitätsanforderungen, Abgabe und Bewertungskriterien 1–15. Die Zuordnung zu den Kriterien steht in Anforderungsmatrix.md.

Team: Daniel Wiebe und Jonas Kölker. Das Aufgabenblatt sieht Dreierteams vor; die Bearbeitung als Zweierteam ist nach Angabe des Teams mit dem Professor abgestimmt und bestätigt. Präsentation und Abgabe: 02.12.2026, Abgabe als ZIP des Abgabestands.

## Kursunterlagen

- 01_Fortgeschrittene_Programmierung_V06-1.pdf
- 02_Versionsverwaltungssysteme-2.pdf
- 03_Einfuehrung-in-Git-2022-1.pdf

## Technische Referenzen und Fremdkomponenten

- Oracle Java SE 25, java.sql.Connection: https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html
- Apache Maven Wrapper: https://maven.apache.org/wrapper/
- Mitgelieferte Wrapper-Skripte: Version 3.3.2 mit Apache-Lizenzhinweisen, unverändert übernommen.
- Maven 3.9.9 mit SHA-256-Prüfung der Distribution: .mvn/wrapper/maven-wrapper.properties
- H2 2.3.232 und JUnit Jupiter 5.11.4: pom.xml

## KI-Unterstützung

Eingesetzt wurde Perplexity Computer. Damit wurden auf Grundlage des Aufgabenblatts und der Kursunterlagen erstellt:

- die Java- und SQL-Dateien (Neuimplementierung, kein Übernehmen einer früheren eigenen Fassung),
- die automatisierten Tests, die Messung und das Refactoring mit Vorher-Nachher-Vergleich,
- die Gegenprobe des Rollback-Tests (nachweise/mutationsprobe.txt),
- Entwürfe der README und der Dokumente in docs/.

Die Protokolle in nachweise/ stammen aus dieser KI-gestützten Erstellung in einer Linux-Umgebung.

Im weiteren Verlauf half Perplexity Computer bei Git-Befehlen, bei der Fehlersuche (fehlgeschlagener Download der Maven-Distribution in GitHub Actions, fehlendes JAVA_HOME auf dem Teamrechner) und bei der Überarbeitung der Dokumente. Dokumentationscommits und Dateilöschungen auf dem Arbeitsbranch hat das Werkzeug jeweils erst nach Freigabe durch Daniel Wiebe ausgeführt.

## Arbeit des Teams

- Daniel Wiebe: Projektstand in Git aufgenommen und zu GitHub übertragen (Importcommit 91699aa, 19.11.2026). Lokalen Build mit Rollback-Test ausgeführt (19.11.2026). Arbeitsbranch aktualisiert, main eingebunden, README-Konflikt aufgelöst und Merge-Commit 09b2e49 gepusht (24.11.2026). Fehlgeschlagenen CI-Lauf ausgewertet und neu gestartet; Build und Demo lokal ausgeführt (26.11.2026).
- Jonas Kölker: nach Angabe des Teams einen eigenen Klon nach README gebaut. Build und Demo am 26.11.2026 gemeinsam mit Daniel Wiebe auf dessen Rechner.
- Beide: Code und Dokumente gelesen und geprüft. Nach eigener Angabe können beide alle Programmteile erklären. Eine eigene Autorschaft einzelner Klassen wird nicht beansprucht.

## Repository

- Repository: https://github.com/danilwie/Sommerkiosk
- Pull Request: https://github.com/danilwie/Sommerkiosk/pull/1
- Prüfstand und CI-Läufe: Pruefstand.md
