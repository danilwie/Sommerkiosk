# Quellen, Ausgangslage und KI-Unterstützung

## Verbindliche und ergänzende Unterlagen

- `04_AufgabeTestat-1.pdf`, „Aufgabe Testat Fortgeschrittene Programmierung“, Aufgabenblatt V4, 5 Seiten: verbindliche Grundlage für die gemeinsame Projektabgabe, insbesondere S. 1–3 und Kriterien 1–15 auf S. 4–5.
- `01_Fortgeschrittene_Programmierung_V06-1.pdf`, WS 2026, 41 Seiten: insbesondere Folien 5–13 (Prüfungsbestandteile und KI), 21–28 (Wartbarkeit und Zusammenarbeit), 35–41 (Projektstart). Der alte Dokumententwurf zitiert eine andere Dateibezeichnung und Folien bis 106; diese Fundstellen gibt es in dieser Datei nicht.
- `02_Versionsverwaltungssysteme-2.pdf`, 116 Seiten: Folie 6 nennt für das **separate VCS-Praktikum** ein Maven-/Gradle-GitHub-Repository, mindestens zwei Branches, ungefähr 15 Commits insgesamt, mindestens zwei Unit-Tests und eine GitHub Action; zufällig gewählte Person erläutert das Repository. Die Tests und der push-ausgelöste Workflow sind technisch vorbereitet; echte Branch-/Commit-Historie und Remote-Lauf sind nicht nachgewiesen. Nicht als zusätzliche Mindestzahl von 15 Commits im Programmier-Aufgabenblatt ausgeben oder nachträglich künstlich erzeugen.
- `03_Einfuehrung-in-Git-2022-1.pdf`, 47 Seiten: Staging, Commits, Branches und Remote-Arbeit. Beispiele im Kursmaterial sind keine Ermächtigung, fremde Änderungen zu überschreiben oder ein öffentliches Repository anzulegen.
- Aktueller Projektauftrag: fachliche Regeln, bestätigte Präsentation am 25.11.2026 und Grenzen für Veröffentlichungen. Kein verbindlicher tatsächlicher Lagerbeginn und kein offizieller Abgabetermin übermittelt.

Der individuelle Java-Feature-Vortrag und die Kurztests sind andere Leistungen. Die Vorlesung nennt für den Feature-Vortrag 7 Minuten Vortrag und 5 Minuten Fragen sowie Themenabstimmung im ILIAS-Wiki. Diese Zeiten sind **nicht** die Vorgabe der Gruppenvorführung. Die hier vorliegenden Projektdateien belegen keine vollständige Themenliste der Gruppe, keine formale Themenanmeldung und keine Erledigung der Einzelleistungen.

## Sichtung der vier Entwurfspaare

| DOCX | PDF | Inhalt und Behandlung |
|---|---|---|
| `01_Projektsteckbrief.docx` | `01_Projektsteckbrief.pdf` | Ziele, Daten und drei fachliche Abläufe übernommen; Status neu formuliert. |
| `02_Projektdokumentation.docx` | `02_Projektdokumentation.pdf` | A01–A10, Kalender-, Geld- und Transaktionsregeln mit Implementierung abgeglichen; Architektur und Zeitstempelspeicherung neu dokumentiert. |
| `03_Arbeitsplan.docx` | `03_Arbeitsplan.pdf` | Vorgeschlagene A/B/C-Verteilung nicht als Teamleistung ausgegeben. |
| `04_Testplan.docx` | `04_Testplan.pdf` | T01–T21 abgeglichen; T08 korrigiert, T09 eingeordnet, T21 bleibt extern offen. |

Die vier Word-Haupttexte und PDF-Texte wurden extrahiert und verglichen. Beim Wortbestandsabgleich enthielten die PDF-Fassungen zusätzlich den wiederholten Seitenfuß mit Datum und Seitenzahl; keine zusätzlichen fachlichen Anforderungen gefunden. Im Abgabepaket stehen nur die neuen Fassungen. `Datenmodell.mmd` war ein Entwurf; die neue Fassung bildet die drei tatsächlich angelegten Tabellen ab. `Vor_Abgabe_lesen.txt` war eine offene Prüfliste; ihre Ergebnisse stehen jetzt in Matrix und Qualitätsnachweis.

In den 14 Anhängen und im bereitgestellten Arbeitsverzeichnis waren weder Java-Quellcode, SQL-Skripte, Build-Dateien noch ein Projekt-Repository vorhanden. Es wurden keine vorhandenen Codeänderungen, Nutzdaten, persönlichen Commits oder historischen CI-Läufe unterstellt. Dieses Projekt ist eine Neuimplementierung auf Basis des Auftrags, keine Prüfung eines nicht vorgelegten Repositorys.

## Technische Referenzen und Fremdkomponenten

JDBC-Schnittstellen und Transaktionsmethoden: [Oracle, Java SE 25, `Connection`](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html). Wrapper und dessen Einrichtung: [Apache Maven Wrapper](https://maven.apache.org/wrapper/). Die mitgelieferten generierten Wrapper-Skripte stammen vom Apache Maven Wrapper Plugin 3.3.2; Lizenzhinweise in den Dateien bleiben erhalten. JDK, Maven, H2 und JUnit sind Fremdwerkzeuge bzw. -bibliotheken; ihre Versionen stehen in README, POM bzw. Wrapper-Konfiguration.

## Abgrenzung der KI-Leistung

Perplexity Computer wurde in diesem Durchlauf für die Sichtung, Neuimplementierung der Java- und SQL-Dateien, Entwurf der Erwartungswerte, automatisierte Tests, lokale Build- und Demoläufe, Messung, Refactoring, Mutationsprobe und Ausarbeitung der Dokumentation eingesetzt. Die Originalunterlagen erwähnen bereits KI-Unterstützung; daraus lassen sich keine individuellen menschlichen Beiträge ableiten. Die grünen Ergebnisse belegen die ausgeführten Tests, nicht das Verständnis oder die Urheberschaft eines Teammitglieds.

Vor Abgabe: KI-Nutzung nach den tatsächlichen Kursregeln offenlegen, echte eigene Prüfungen und Änderungen der Teammitglieder ergänzen und alle Programmteile selbst erläutern können. Kein Name, kein Commit und keine Arbeitszeit ist stellvertretend für eine Person erfunden worden.
