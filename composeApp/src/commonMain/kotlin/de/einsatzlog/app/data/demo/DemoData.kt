package de.einsatzlog.app.data.demo

import de.einsatzlog.app.data.EinsatzRepository
import de.einsatzlog.app.domain.MessageType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private data class DemoEntry(
    val minutesAfterStart: Int,
    val type: MessageType,
    val source: String?,
    val target: String?,
    val message: String,
)

// Realistic Zimmerbrand scenario, adapted from the predecessor's
// ai-prompts scenario data (fire-fighting-scenario-data-structure.md).
private val demoEntries = listOf(
    DemoEntry(0, MessageType.FUNKSPRUCH, "Leitstelle", "Florian 1", "Zimmerbrand gemeldet, Hauptstraße 45, EG, Person möglicherweise im Gebäude"),
    DemoEntry(4, MessageType.FUNKSPRUCH, "Florian 1", "Leitstelle", "Florian 1 am Einsatzort eingetroffen, Lageerkundung beginnt"),
    DemoEntry(6, MessageType.LAGEMELDUNG, "Florian 1", "Leitstelle", "Lage: Zimmerbrand im EG, starke Rauchentwicklung, Menschenrettung eingeleitet"),
    DemoEntry(8, MessageType.AUFTRAG, "Einsatzleiter", "Trupp 1", "Innenangriff über Haupteingang, Brandbekämpfung Erdgeschoss"),
    DemoEntry(12, MessageType.ANFORDERUNG, "Florian 1", "Leitstelle", "Benötigen Rettungsdienst zur Absicherung, eine Person mit Rauchgasverdacht"),
    DemoEntry(18, MessageType.FUNKSPRUCH, "Trupp 1", "Einsatzleiter", "Menschenrettung abgeschlossen, alle Bewohner unverletzt"),
    DemoEntry(25, MessageType.LAGEMELDUNG, "Einsatzleiter", "Leitstelle", "Feuer unter Kontrolle, keine Gefahr für Nachbarbebauung"),
    DemoEntry(41, MessageType.DOKUMENTATION, null, null, "Brand im Erdgeschoss unter Kontrolle, Nachlöscharbeiten im Gang"),
    DemoEntry(55, MessageType.LAGEMELDUNG, "Einsatzleiter", "Leitstelle", "Einsatz beendet, Brandschutzwache eingerichtet"),
)

/**
 * Seeds a clearly-labeled demo Einsatz (spec 002) so first-time users and
 * judges see a realistic filled logbook. Backdated ~1h; the Einsatz is closed.
 */
@OptIn(ExperimentalTime::class)
suspend fun seedDemoEinsatz(repository: EinsatzRepository, demoName: String): String {
    val start = Clock.System.now().toEpochMilliseconds() - 60 * 60 * 1000
    val id = repository.createEinsatz(
        name = demoName,
        description = "Beispieldaten – kann gelöscht werden",
        startedAtEpochMs = start,
    )
    demoEntries.forEach { e ->
        repository.addEntry(
            einsatzId = id,
            type = e.type,
            source = e.source,
            target = e.target,
            message = e.message,
            timestampEpochMs = start + e.minutesAfterStart * 60_000L,
        )
    }
    repository.closeEinsatz(id)
    return id
}
