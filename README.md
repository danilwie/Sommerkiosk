# Sommerkiosk

Java-Konsolenanwendung für den Kiosk eines zehntägigen Sommerlagers.

Team: Daniel Wiebe und Jonas Kölker  
Modul: Fortgeschrittene Programmierung  
Version: 1.0.0  
Präsentation und Abgabe: 02.12.2026  
Repository: https://github.com/danilwie/Sommerkiosk

## Funktionen und Grenzen

1. Stammdaten verwalten: Kinder und Artikel anlegen, Kinder suchen, Artikelpreise und Verfügbarkeit ändern und unreferenzierte Artikel löschen.
2. Käufe verbuchen: Kind und Artikel auswählen, bestätigen und Kauf samt Guthabenabzug gemeinsam speichern.
3. Abrechnung anzeigen: historische Käufe, Tagesausgaben und geprüfte Rückzahlungsbeträge ausgeben.

Keine Auszahlungsbuchung, Stornos, Bestandszählung oder parallele Kassenbedienung. Kinder werden durch IDs unterschieden; gleiche Namen sind zulässig.

## Fachregeln

- Startbudget: 1.500 Cent je Kind.
- Tageslimit: 150 Cent je Kalendertag in Europe/Berlin, ohne Übertrag.
- Das Lager umfasst den Starttag und neun Folgetage.
- Ein Kauf entspricht genau einem Artikel. Nur verfügbare Artikel sind kaufbar.
- Geld wird in ganzen Cent verarbeitet. Der historische Kaufpreis bleibt bei Preisänderungen unverändert.
- Kauf und Budgetabzug bilden eine Transaktion; ein Fehler führt zum Rollback-Versuch und einer Diagnose.

## Voraussetzungen und Build

JDK 25 mit java und javac im PATH beziehungsweise korrekt gesetztem JAVA_HOME. IDE: IntelliJ oder Eclipse mit JDK 25; alternativ Terminal. Im Projektordner müssen pom.xml, .mvn, mvnw und mvnw.cmd vorhanden sein. Versteckte Dateien beim Kopieren mitnehmen.

Der Wrapper verwendet Maven 3.9.9; die POM legt H2 2.3.232 und JUnit Jupiter 5.11.4 fest. Kein separater Datenbankserver erforderlich. Der erste Build benötigt Internetzugriff auf Maven Central und Schreibrechte im Projekt sowie Maven-Benutzerverzeichnis.

Windows PowerShell:

```powershell
.\mvnw.cmd -B -ntp clean verify
```

Linux/macOS:

```bash
sh ./mvnw -B -ntp clean verify
```

Erwartet: BUILD SUCCESS mit 24 Tests (RegelnTest 5, KioskDbTest 18, MessungTest 1), ohne Fehler oder übersprungene Tests.

Windows: Meldet der Wrapper, JAVA_HOME sei nicht korrekt gesetzt, für die Sitzung auf das JDK 25 zeigen lassen, zum Beispiel:

```powershell
$env:JAVA_HOME = "C:\Pfad\zum\jdk-25"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
```

JAR: target/sommerkiosk-1.0.0.jar. Testberichte: target/surefire-reports/.

## Drei Abläufe demonstrieren

Frische Demo starten:

```text
java -jar target/sommerkiosk-1.0.0.jar --demo
```

Die Demo verwendet eine getrennte In-Memory-Datenbank und das feste Berliner Datum 03.07.2026. Sie berührt keine Nutzdatei. Die folgenden Befehle in derselben frischen Sitzung eingeben; jede Zeile einzeln:

```text
suche|Alex
liste
artikel|Probiertee|0,60
aendern|6|0,70|ja
loeschen|6
kauf|1|2
ja
kauf|1|4
ja
auszug|1
abschluss
ende
```

- Stammdaten: zwei Alex mit unterschiedlichen IDs; der neue Artikel erhält in der frischen Demo ID 6 und wird nach Änderung wieder gelöscht. Ein benutzter Artikel, etwa ID 2, darf nicht gelöscht werden.
- Kauf: Alex Nr. 1 hat heute bereits 100 Cent ausgegeben. Der Apfel zu 50 Cent wird gebucht: heute 150 Cent, Rest 1.350 Cent. Der folgende Wasserkauf zu 25 Cent wird abgelehnt; der Zustand bleibt unverändert.
- Abrechnung: Der Auszug zeigt Saft und Apfel zu den historischen Preisen. Der Gesamtrückzahlungsbetrag beträgt nach diesem Ablauf 70,75 Euro; dies ist keine Auszahlung.

Ein neuer Demo-Prozess stellt den ursprünglichen Bestand wieder her. Die gleichen Eingaben liegen in demo/eingaben.txt.

Automatisch in PowerShell (die erste Zeile sorgt für korrekte Umlaute und €):

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
Get-Content demo/eingaben.txt | java -jar target/sommerkiosk-1.0.0.jar --demo
```

Linux/macOS:

```bash
java -jar target/sommerkiosk-1.0.0.jar --demo < demo/eingaben.txt
```

## Dateibetrieb und Datenaufbau

```text
java -jar target/sommerkiosk-1.0.0.jar --lager 2026-07-01
```

Das Datum ist nur ein Beispiel. Für den Einsatz den tatsächlichen Lagerbeginn angeben. Daten liegen relativ zum Arbeitsverzeichnis in data/kiosk-<Lagerbeginn>.mv.db. Gleiches Verzeichnis und gleiches Datum öffnen denselben Bestand. Außerhalb des Lagerzeitraums bleiben Verwaltung und Auskunft möglich, Käufe nicht. Mit ende beenden. Vor einer Sicherung die Anwendung schließen; keine Datenbank- oder Sperrdatei auf Verdacht löschen.

| Modus | Aufbau | Ausgangszustand |
|---|---|---|
| Dateibetrieb | src/main/resources/db/schema.sql | Kein Seed; vorhandene Daten bleiben erhalten |
| Demo | Schema, dann src/main/resources/db/demo.sql | Neuer Demo-Prozess |
| Tests | Schema, dann src/test/resources/db/testdaten.sql | Je Test frische H2-Instanz; Dateitest im temporären Ordner |

Seeds nur einmal in eine neue leere Datenbank einspielen. Beide Seeds enthalten je fünf Kinder, Artikel und Käufe. Test- und Nutzdaten bleiben getrennt.

## Qualitätsnachweise

GitHub Actions (.github/workflows/build.yml) baut und testet jeden Push und Pull Request mit einer frischen H2-Testdatenbank.

- Anforderungszuordnung und Normal-/Fehlerfälle: docs/Anforderungsmatrix.md
- Schema: src/main/resources/db/schema.sql
- Modell: docs/Datenmodell.mmd
- Messung und Refactoring: docs/Qualitaet.md und nachweise/refactoring.diff
- CI-Läufe, lokaler Lauf und Protokolle: docs/Pruefstand.md und nachweise/
- Quellen und KI: docs/Quellen-und-KI.md
- Projektsteckbrief, Projektdokumentation und Arbeitsplan: docs/01_Projektsteckbrief, docs/02_Projektdokumentation, docs/03_Arbeitsplan jeweils als DOCX/PDF

nachweise/SHA256SUMS.txt enthält die Prüfsummen aller Dateien des Abgabestands. Prüfen unter Linux/macOS: `sha256sum -c nachweise/SHA256SUMS.txt`.

Persönliche Daten, lokale Datenbanken und Zugangsdaten nicht committen. Bei release version 25 not supported das von Maven verwendete JDK prüfen. Bei Downloadfehlern Ursache prüfen; TLS-Prüfungen nicht deaktivieren. Bei unklarem Kaufabschluss zuerst den Auszug prüfen, nicht blind erneut buchen.
