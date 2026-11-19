# Gruppenvorführung am 25.11.2026

Leitfrage: Wie verhindert der Kiosk, dass eine abgelehnte Buchung im Kassenstand stehen bleibt? Vorschlag: sieben Minuten Vorführung plus Fragen, falls die Lehrperson keine andere Dauer nennt. Das ist keine offizielle Zeitvorgabe für die Gruppendemo. Alle drei Personen sollen ohne Rollenwechsel vorführen und erklären können.

| Zeit | Zeigen | Sagen oder prüfen |
|---|---|---|
| 0:00–0:45 | Steckbrief, ER-Modell | „Wir berechnen Rückzahlungsbeträge; wir zahlen nicht aus. Ein Kauf verbindet genau ein Kind und einen Artikel.“ |
| 0:45–1:45 | Frische Demo starten; `suche\|Alex`, `liste`; Tee anlegen, ändern, löschen wie in README | Zwei gleichnamige Kinder, unterschiedliche Guthaben und IDs. Unbenutzten Artikel löschen ist zulässig. |
| 1:45–3:00 | `kauf\|1\|2`, `ja`; anschließend `kauf\|1\|4`, `ja` | 100 + 50 = 150 Cent erlaubt; weitere 25 Cent abgelehnt. Erst nach Commit kommt „Gebucht“. |
| 3:00–3:45 | `auszug\|1`, `abschluss`, `ende` | Zeige die beiden historischen Preise, 13,50 € Einzelrest und 70,75 € Gesamtrest. Keine Auszahlung behaupten. |
| 3:45–5:10 | In IDE `rollbackNachInsert` und `KaufService.buchen` nebeneinander; Einzeltest aus README starten | Erster Write erfolgreich, zweiter Write verletzt Test-Constraint. Neue Verbindungen sehen keinen zusätzlichen Kauf und keinen Budgetabzug. Im Mutanten hinterlässt ein früher Commit einen Kauf. |
| 5:10–6:15 | `MessungTest`, danach `nachweise/refactoring.diff` | Messgröße ist Zahl der SELECTs, nicht Laufzeit. Eine Abfrage für jede Datenmenge. Zeige ID-Code vorher/nachher und unveränderte Tests. |
| 6:15–7:00 | Lokalen Gesamtbericht, Workflow und ggf. echten CI-Lauf zeigen | 5 Fachtests + 18 DB-Suite + 1 H2-Messung. Ohne überprüften Remote-Lauf nur „Workflow vorbereitet“ sagen. |

Vorbereitung: Alle drei führen die README einmal aus einem neuen Ordner aus. JDK, Abhängigkeiten und Build schon vor der Präsentation laden. Drei Tabs bereithalten: Terminal, `KaufService` mit Test, Matrix mit Nachweisen. Falls sich jemand vertippt: `ende`, `--demo` erneut starten und von vorn; kein Löschen persönlicher Daten. Ein alter Screenshot ersetzt keinen echten CI-Lauf.

## Technischer Rundgang

Starte bei `Main`: Der Modus wählt Datenbank und `Clock`. `Menue` zerlegt eine Zeile, zeigt eine Kaufvorschau und wartet auf Bestätigung. Es kennt Aufgaben der Services, aber keine INSERT-SQL-Texte. `Stammdaten` kapselt die SQL-Operationen für Verwaltung und Suche. Die Fachregel `Regeln.pruefeKauf` ist ohne Datenbank testbar. `KaufService` besitzt die Transaktionsgrenze und nutzt die verbindlichen aktuellen Daten. `Jdbc` bindet Werte und schließt Statements, aber nicht die geliehene Connection. `Abrechnung` kontrolliert die unabhängig rekonstruierte Summe, bevor Zahlen ausgegeben werden.

## Mögliche Prüfungsfragen mit Antworten

1. **Warum ist der Name kein Schlüssel?** Zwei Kinder können Alex heißen. `kind_id` identifiziert eindeutig; die Suche kann bewusst mehrere Treffer liefern.
2. **Was speichern die beiden Fremdschlüssel?** Jeder Kauf bezeichnet genau ein Kind und genau einen Artikel. H2 verhindert verwaiste Käufe; ein benutzter Artikel bleibt erhalten.
3. **Wo ist CRUD zu sehen?** Kind/Artikel anlegen, per Suche/Liste lesen, Artikelpreis und Verfügbarkeit ändern, unreferenzierten Artikel löschen. CRUD gilt insgesamt, nicht für jede Tabelle einzeln.
4. **Was bewirkt `LOCATE` mit einem Parameter?** Der Suchteil wird als Wert gebunden, nicht in SQL eingesetzt. `%` und `_` sind beim Teilstringvergleich normale Zeichen; leere Suche zeigt alle.
5. **Warum keine `double`-Preise?** Wir benötigen exakte Cent. `BigDecimal` entsteht aus dem geprüften Eingabetext; `movePointRight(2).intValueExact()` liefert Cent oder lehnt einen Überlauf ab.
6. **Warum steht der Preis zweimal im Modell?** Der Artikelpreis ist das aktuelle Angebot, der Kaufpreis ist ein historischer Beleg. Ein UPDATE des Angebots darf kein altes Guthaben ändern.
7. **Wozu das gespeicherte Restbudget?** Es ist der fortgeschriebene Stand des Kinderkontos und der zweite fachliche Schreibschritt. `Abrechnung` prüft ihn gegen Startbudget minus Summe der Kaufpreise.
8. **Warum reicht ein `INSERT` mit anschließendem unabhängigem `UPDATE` nicht?** Ein Fehler dazwischen würde Geld und Historie trennen. Beide Writes nutzen dieselbe Connection mit deaktiviertem Auto-Commit.
9. **Warum nach dem UPDATE die Zeilenzahl prüfen?** Null wäre kein erfolgreicher Abzug, selbst ohne SQL-Exception. Die Bedingung prüft außerdem den gelesenen Altstand. Dann muss auch der Kauf zurückgerollt werden.
10. **Warum keine expliziten Zeilensperren?** Eine lokale CLI, eine sequenzielle Sitzung, kein Servermodus. Der Altstandsvergleich ist ein zusätzlicher Schutz; wir beanspruchen keine vollständige Mehrplatzgarantie.
11. **Wie unterscheidet der Test echte Rücknahme von früher Ablehnung?** Er erwartet die benannte CHECK-Verletzung des zweiten Writes statt einer `Ablehnung` und liest anschließend beide Tabellen unabhängig. Der vorzeitig commitende Mutant wird rot.
12. **Was passiert, wenn Rollback selbst scheitert?** Der erste Fehler bleibt Hauptursache, der Rollbackfehler wird mit `addSuppressed` angehängt. Die Connection wird geschlossen. Vor Wiederholung wird der tatsächliche Bestand geprüft.
13. **Warum kein Tageswert als eigenes Guthaben?** Er ist eine Obergrenze für die Summe des lokalen Tages. Am nächsten Tag beginnt die Summe bei null, unabhängig davon, was am Vortag übrig blieb.
14. **Wieso die halb offene Zeitspanne?** Von lokalem Mitternachtspunkt einschließlich bis zum nächsten ausschließlich. Ein Kauf um genau 00:00 gehört nur zum neuen Tag; Kalendertage sind nicht immer exakt 24 Stunden lang.
15. **Wie bleibt die Demo im November fachlich gültig?** Eine feste Uhr simuliert den dritten fiktiven Lagertag. Dieselbe Regel prüft ihn gegen den fiktiven Start, Nutzdaten bleiben unberührt.
16. **Wie zeigt ihr den letzten zulässigen Kauf?** Neun Tage à 150 Cent, dann 100 Cent am letzten Tag. Von den verbleibenden 50 Cent sind 51 zu viel; genau 50 sind zulässig. Heute null bei vorher 1.450 ist nicht erreichbar.
17. **Warum `LEFT JOIN` in der Abschlussliste?** Auch Kinder ohne Kauf sollen erscheinen. `COALESCE(SUM(...),0)` macht aus fehlenden Kaufbeträgen null Cent, nicht SQL-NULL.
18. **Was zeigt die Messung nicht?** Eine SELECT-Ausführung pro Abschlussliste heißt nicht konstante Laufzeit. H2 verarbeitet wachsende Datenmengen, Java erzeugt mehr Ergebnisobjekte.
19. **Warum laufen die DB-Tests in Surefire?** Alle drei Klassen enden auf `Test`; sie werden in der Phase `test` erfasst. `clean verify` baut danach das ausführbare JAR, die CI kontrolliert die drei Reportdateien zusätzlich.
20. **Wie belegt ihr das Refactoring ohne alten Git-Commit?** Eine funktionierende erste Version wurde vor der Änderung gesichert und getestet. Realer Diff und zwei lokale grüne Läufe belegen die Änderung, nicht eine erfundene Teamhistorie.
21. **Welche Grenze hat die Abrechnung?** Ein berechneter Betrag ist kein Buchungsbeleg für eine Auszahlung. Bei widersprüchlichem Restbudget gibt es eine technische Diagnose statt einer verlässlichen Gesamtsumme.

Übung für das Team: Erst die erwarteten Zeilen und Beträge für den Apfelkauf ohne Programm vorhersagen. Dann den Service bis zur Transaktion verfolgen und erklären, welche konkrete Assertion einen vergessenen Abzug, einen frühen Commit oder einen geänderten historischen Preis erkennt.
