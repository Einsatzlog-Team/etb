package de.einsatzlog.screenshots

import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.domain.MessageType

/**
 * The scenario the store listing tells: a night-time structure fire with a
 * person rescued, run over the radio the way a real Einsatz is.
 *
 * Kept here rather than in `:ui`'s preview fixtures on purpose — previews want
 * the smallest data that exercises a component, listing images want a screen
 * that looks like a real operation and fills a 2868px-tall canvas. All
 * timestamps are fixed, so re-rendering is byte-stable.
 *
 * Call signs follow German fire-service convention (ELW = command vehicle,
 * HLF = pump/rescue, DLK = turntable ladder, RTW/NEF = ambulance/doctor).
 */

/** 2026-07-18, 19:42 Europe/Berlin — the alarm. */
private const val ALARM = 1_784_396_520_000L
private const val MIN = 60_000L

private fun einsatz(
    id: String,
    name: String,
    description: String?,
    startedAt: Long,
    durationMin: Long?,
) = EinsatzEntity(
    id = id,
    name = name,
    description = description,
    startedAtEpochMs = startedAt,
    endedAtEpochMs = durationMin?.let { startedAt + it * MIN },
    createdAtEpochMs = startedAt,
)

val demoZimmerbrand = einsatz(
    id = "e-zimmerbrand",
    name = "Zimmerbrand Hauptstraße 14",
    description = "Person gerettet · 3 Trupps · DLK 23",
    startedAt = ALARM,
    durationMin = null, // still running — this is the "aktiv" card
)

/** Newest first, the way the home list sorts. */
val demoEinsaetze = listOf(
    demoZimmerbrand,
    einsatz(
        "e-vu", "Verkehrsunfall B3, Abfahrt Nord",
        "Zwei Fahrzeuge · eine Person eingeklemmt",
        1_784_265_120_000L, 94,
    ),
    einsatz(
        "e-oel", "Ölspur Ludwigstraße",
        "Ca. 400 m · Bindemittel gestreut",
        1_784_116_980_000L, 51,
    ),
    einsatz(
        "e-bma", "Ausgelöste BMA Schulzentrum",
        "Fehlalarm · Küchendampf",
        1_783_819_560_000L, 38,
    ),
    einsatz(
        "e-sturm", "Sturmschaden Waldstraße",
        "Baum auf Fahrbahn · Kettensäge",
        1_783_612_080_000L, 67,
    ),
    einsatz(
        "e-uebung", "Übung: Menschenrettung Brandhaus",
        "Jahresübung der Feuerwehr",
        1_783_148_400_000L, 180,
    ),
)

private fun entry(
    n: Int,
    afterMin: Long,
    type: MessageType,
    source: String?,
    target: String?,
    message: String,
) = LogEntryEntity(
    id = "l-%02d".format(n),
    einsatzId = demoZimmerbrand.id,
    timestampEpochMs = ALARM + afterMin * MIN,
    type = type,
    source = source,
    target = target,
    message = message,
    createdAtEpochMs = ALARM + afterMin * MIN,
)

/** Long enough to fill a 6.9" iPhone and a 13" iPad without dead space. */
val demoEntries = listOf(
    entry(1, 0, MessageType.FUNKSPRUCH, "Leitstelle", "ELW 1",
        "Alarmierung: Zimmerbrand Hauptstraße 14, Person vermisst"),
    entry(2, 5, MessageType.LAGEMELDUNG, "ELW 1", "Leitstelle",
        "Eintreffen. Rauch aus dem 1. OG, Ausbreitung auf Dachstuhl möglich"),
    entry(3, 7, MessageType.AUFTRAG, "EL", "Angriffstrupp",
        "Innenangriff über Treppenraum, erstes Rohr, Menschenrettung"),
    entry(4, 9, MessageType.AUFTRAG, "EL", "Wassertrupp",
        "Wasserversorgung ab Hydrant Ecke Ludwigstraße aufbauen"),
    entry(5, 12, MessageType.ANFORDERUNG, "ELW 1", "Leitstelle",
        "DLK 23 und zweiten RTW nachfordern"),
    entry(6, 16, MessageType.LAGEMELDUNG, "Angriffstrupp", "EL",
        "Person im 1. OG gefunden, Rettung läuft"),
    entry(7, 21, MessageType.LAGEMELDUNG, "ELW 1", "Leitstelle",
        "Person gerettet, Übergabe an Rettungsdienst"),
    entry(8, 24, MessageType.FUNKSPRUCH, "DLK 23", "EL",
        "Einsatzbereit, Anleiterbereitschaft Gebäuderückseite"),
    entry(9, 29, MessageType.LAGEMELDUNG, "EL", "Leitstelle",
        "Feuer unter Kontrolle"),
    entry(10, 37, MessageType.AUFTRAG, "EL", "Angriffstrupp",
        "Nachlöscharbeiten, Dachstuhl mit Wärmebildkamera kontrollieren"),
    entry(11, 46, MessageType.DOKUMENTATION, null, null,
        "Wärmebildkamera: keine Glutnester im Dachstuhl"),
    entry(12, 52, MessageType.LAGEMELDUNG, "EL", "Leitstelle",
        "Feuer aus"),
    entry(13, 58, MessageType.ANFORDERUNG, "ELW 1", "Leitstelle",
        "Energieversorger zur Abschaltung anfordern"),
    entry(14, 70, MessageType.DOKUMENTATION, null, null,
        "Einsatzstelle an Polizei übergeben"),
)

val demoEntryCounts: Map<String, Int> = mapOf(
    demoZimmerbrand.id to demoEntries.size,
    "e-vu" to 21,
    "e-oel" to 8,
    "e-bma" to 6,
    "e-sturm" to 12,
    "e-uebung" to 34,
)
