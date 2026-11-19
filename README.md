# Sommerkiosk

Java-Konsolenanwendung zur Verwaltung eines Kiosks in einem zehntägigen Sommerlager.

**Team:** Daniel Wiebe und Jonas Kölker  
**Modul:** Fortgeschrittene Programmierung  
**Version:** 1.0.0  
**Präsentation:** 25.11.2026  
**Repository:** https://github.com/danilwie/Sommerkiosk

## Funktionsumfang

Die Anwendung unterstützt drei zusammenhängende Anwendungsfälle:

- Stammdaten verwalten: Kinder und Artikel anlegen, Kinder suchen sowie Artikelpreise und Verfügbarkeit ändern. Unreferenzierte Artikel können gelöscht werden.
- Käufe verbuchen: Kind und Artikel auswählen, Fachregeln prüfen und Kauf sowie Guthabenabzug gemeinsam speichern.
- Abrechnung anzeigen: Kaufhistorie, Tagesausgaben und Rückzahlungsbeträge ausgeben.

Eine tatsächliche Auszahlung wird nicht erfasst. Lagerbestandszählung, Stornos und parallele Kassenbedienung sind nicht Bestandteil des Projekts.

## Fachregeln

- Startguthaben je Kind: 15,00 €.
- Tageslimit: 1,50 € pro Kalendertag in `Europe/Berlin`.
- Nicht genutztes Tagesbudget wird nicht übertragen.
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

Bedienung, Fachlogik und Datenzugriff sind in getrennten Klassen organisiert. Das Datenmodell besteht aus `kind`, `artikel` und `kauf`.

## Build und Tests

Voraussetzung ist ein installiertes JDK 25. Der erste Build benötigt Internetzugriff zum Laden der Abhängigkeiten.

### Windows

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

## Qualitätssicherung

Der dokumentierte lokale Gesamtbuild vom 19.11.2026 umfasst 24 erfolgreiche Tests:

| Testklasse | Anzahl |
|---|---:|
| `RegelnTest` | 5 |
| `KioskDbTest` | 18 |
| `MessungTest` | 1 |

Es traten keine Fehler oder übersprungenen Tests auf. Ein zusätzlicher gezielter Lauf des Rollback-Tests war ebenfalls erfolgreich.

Für Importcommit `91699aa` wurde der GitHub-Actions-Job `verify` erfolgreich abgeschlossen:

https://github.com/danilwie/Sommerkiosk/actions/runs/44561539453

Dieser Nachweis bezieht sich auf den genannten Commit. Spätere Änderungen benötigen einen eigenen erfolgreichen Prüflauf.

## Projektstruktur

| Verzeichnis | Inhalt |
|---|---|
| `src/main/java/kiosk/` | Anwendungscode |
| `src/main/resources/db/` | Schema und Demo-Daten |
| `src/test/java/kiosk/` | Fachtests, Datenbanktests und Messung |
| `src/test/resources/db/` | Testdaten |
| `.github/workflows/` | CI-Konfiguration |
| `demo/` | Vorbereitete Konsoleneingaben |
| `docs/` | Projektdokumentation und Präsentationsunterlagen |
| `nachweise/` | Prüfprotokolle und Refactoring-Nachweis |

## Dokumentation

- Projektsteckbrief: `docs/01_Projektsteckbrief`
- Projektdokumentation: `docs/02_Projektdokumentation`
- Arbeitsplan und Beiträge: `docs/03_Arbeitsplan`
- Testplan: `docs/04_Testplan`
- Anforderungsmatrix: `docs/Anforderungsmatrix.md`
- Datenmodell: `docs/Datenmodell.mmd`
- Messung und Refactoring: `docs/Qualitaet.md`
- Präsentation: `docs/Praesentation.md`
- Quellen und KI-Unterstützung: `docs/Quellen-und-KI.md`

Die nummerierten Dokumente liegen als DOCX und PDF vor.

Persönliche Daten, lokale Datenbankdateien und Zugangsdaten gehören nicht ins Repository.
