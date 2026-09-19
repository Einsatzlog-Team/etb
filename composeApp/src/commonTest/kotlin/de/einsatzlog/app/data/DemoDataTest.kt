package de.einsatzlog.app.data

import de.einsatzlog.app.data.demo.seedDemoEinsatz
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class DemoDataTest {

    @Test
    fun seedsClosedEinsatzWithChronologicalEntries() = runTest {
        val einsatzDao = FakeEinsatzDao()
        val logDao = FakeLogEntryDao()
        var counter = 0
        val repo = EinsatzRepository(
            einsatzDao = einsatzDao,
            logEntryDao = logDao,
            newId = { "id-${counter++}" },
        )

        val id = seedDemoEinsatz(repo, demoName = "Demo: Zimmerbrand")

        val einsatz = einsatzDao.items.value.single()
        assertEquals(id, einsatz.id)
        assertTrue(einsatz.name.startsWith("Demo:"), "demo must be clearly labeled")
        assertTrue(einsatz.endedAtEpochMs != null, "demo Einsatz is closed")

        val entries = logDao.items.value
        assertTrue(entries.size >= 8)
        assertEquals(entries.map { it.timestampEpochMs }.sorted(), entries.map { it.timestampEpochMs })
    }
}
