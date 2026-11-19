# Sommerkiosk

Java-Konsolenanwendung zur Verwaltung eines Kiosks in einem zehntägigen Sommerlager.

Team: Daniel Wiebe und Jonas Kölker  
Modul: Fortgeschrittene Programmierung  
Version: 1.0.0  
Präsentation: 25.11.2026  
Repository: https://github.com/danilwie/Sommerkiosk

## Funktionsumfang

- Stammdaten verwalten: Kinder und Artikel anlegen, Kinder suchen sowie Artikelpreise und Verfügbarkeit ändern. Unreferenzierte Artikel können gelöscht werden.
- Käufe verbuchen: Kind und Artikel auswählen, Fachregeln prüfen und Kauf sowie Guthabenabzug gemeinsam speichern.
- Abrechnung anzeigen: Kaufhistorie, Tagesausgaben und Rückzahlungsbeträge ausgeben.

Eine tatsächliche Auszahlung wird nicht erfasst. Lagerbestandszählung, Stornos und parallele Kassenbedienung sind nicht Bestandteil des Projekts.

## Fachregeln

- Startguthaben je Kind: 15,00 €.
- Tageslimit: 1,50 € pro Kalendertag in Europe/Berlin. Nicht genutztes Tagesbudget wird nicht übertragen.
- Der Lagerzeitraum umfasst zehn Tage.
- Ein Kauf entspricht genau einem Artikel.
- Kinder werden durch eindeutige IDs identifiziert; gleiche Namen sind zulässig.
- Historische Kaufpreise bleiben bei späteren Preisänderungen erhalten.
- Geldbeträge werden als ganze Cent verarbeitet.
- Kauf und Guthabenabzug werden in einer gemeinsamen Transaktion gespeichert. Bei einem Fehler erfolgt ein Rollback.

## Technischer Aufbau

| Komponente | Verwendung |
|---|---|
| Java 25 | Anwendung |
| Maven 3.9.9 mit Wrapper | Build und Abhängigkeiten |
| H2 2.3.232 | Relationale Datenbank |
| JDBC | Datenzugriff und Transaktionen |
| JUnit Jupiter 5.11.4 | Automatisierte Tests |
| GitHub Actions | Automatisierter Build und Tests |

Bedienung, Fachlogik und Datenzugriff sind in getrennten Klassen organisiert. Das Datenmodell besteht aus kind, artikel und kauf.

## Build und Tests

Voraussetzung ist ein installiertes JDK 25. Der erste Build benötigt Internetzugriff zum Laden der Abhängigkeiten.

### Windows PowerShell

```powershell
.\mvnw.cmd -B -ntp clean verify
```

### Linux und macOS

```bash
sh ./mvnw -B -ntp clean verify
```

Das ausführbare JAR wird unter `target/sommerkiosk-1.0.0.jar` erstellt. Testberichte liegen unter `target/surefire-reports/`.

## Anwendung starten

### Demo

```text
java -jar target/sommerkiosk-1.0.0.jar --demo
```

Der Demo-Modus verwendet vorbereitete Daten in einer getrennten In-Memory-Datenbank und das feste Datum 03.07.2026. Ein neuer Demo-Prozess stellt den ursprünglichen Bestand wieder her.

Vorbereitete Konsoleneingaben befinden sich in `demo/eingaben.txt`.

### Dateibasierter Betrieb

Beispiel mit einem ausdrücklich beispielhaften Lagerbeginn:

```text
java -jar target/sommerkiosk-1.0.0.jar --lager 2026-07-01
```

Für den tatsächlichen Einsatz ist das Datum durch den realen Lagerbeginn zu ersetzen.

Die Daten werden relativ zum Arbeitsverzeichnis unter `data/kiosk-<Lagerbeginn>.mv.db` gespeichert. Vorhandene Bestände bleiben beim Neustart erhalten. Außerhalb des Lagerzeitraums sind Verwaltung und Auskunft möglich, Käufe jedoch nicht.

Die Anwendung wird mit `ende` beendet.

## Qualitätssicherung und Prüfstand

Der gelesene Testquelltext enthält 24 Testmethoden:

| Testklasse | Anzahl |
|---|---:|
| RegelnTest | 5 |
| KioskDbTest | 18 |
| MessungTest | 1 |

Für den vor dieser Dokumentkorrektur geprüften PR-Stand `178773bfac644d38f0aa07705002878c7b63009c` meldete GitHub am 19.11.2026 beide verify-Checks als erfolgreich:

- Push-Lauf: https://github.com/danilwie/Sommerkiosk/actions/runs/44569977906
- PR-Lauf nach Wiederholung: https://github.com/danilwie/Sommerkiosk/actions/runs/44570965319

Der erste Versuch des PR-Laufs scheiterte beim Download der Maven-Distribution, nicht mit einem nachgewiesenen Java-Testfehler. Die Wiederholung war erfolgreich. Die Check-Ergebnisse sind keine neue lokale Testausführung durch den Assistenten und kein Nachweis für spätere Commits.

Prüfumfang, Einschränkungen und offene Aufgaben stehen in [Pruefstand](docs/Pruefstand.md).

## Projektstruktur

| Verzeichnis | Inhalt |
|---|---|
| src/main/java/kiosk/ | Anwendungscode |
| src/main/resources/db/ | Schema und Demo-Daten |
| src/test/java/kiosk/ | Fachtests, Datenbanktests und Messung |
| src/test/resources/db/ | Testdaten |
| .github/workflows/ | CI-Konfiguration |
| demo/ | Vorbereitete Konsoleneingaben |
| docs/ | Projektdokumentation und Präsentationsunterlagen |
| nachweise/ | Vorhandene Prüfprotokolle und Refactoring-Nachweis |

## Dokumentation

- Projektsteckbrief: `docs/01_Projektsteckbrief.docx` und `.pdf`
- Projektdokumentation: `docs/02_Projektdokumentation.docx` und `.pdf`
- Arbeitsplan und Beiträge: `docs/03_Arbeitsplan.docx` und `.pdf`
- Testplan: `docs/04_Testplan.docx` und `.pdf`
- Anforderungsmatrix: `docs/Anforderungsmatrix.md`
- Datenmodell: `docs/Datenmodell.mmd`
- Messung und Refactoring: `docs/Qualitaet.md`
- Präsentation: `docs/Praesentation.md`
- Quellen und KI-Unterstützung: `docs/Quellen-und-KI.md`
- Aktueller Prüfstand und offene Aufgaben: `docs/Pruefstand.md`

Das Vorhandensein von DOCX und PDF bestätigt nicht deren inhaltliche Übereinstimmung. Der Abgleich des neu ersetzten Arbeitsplans ist offen.

Persönliche Daten, lokale Datenbankdateien und Zugangsdaten gehören nicht ins Repository.
