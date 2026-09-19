package de.einsatzlog.app.data

import de.einsatzlog.app.domain.MessageType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class EinsatzRepositoryTest {

    private fun repository(einsatzDao: FakeEinsatzDao, logDao: FakeLogEntryDao): EinsatzRepository {
        var counter = 0
        return EinsatzRepository(
            einsatzDao = einsatzDao,
            logEntryDao = logDao,
            newId = { "id-${counter++}" },
            nowEpochMs = { 1_000_000L },
        )
    }

    @Test
    fun addEntry_toClosedEinsatz_throws() = runTest {
        val einsatzDao = FakeEinsatzDao()
        val logDao = FakeLogEntryDao()
        val repo = repository(einsatzDao, logDao)

        val id = repo.createEinsatz("Übung Brandhaus")
        repo.closeEinsatz(id)

        assertFailsWith<EinsatzClosedException> {
            repo.addEntry(id, MessageType.FUNKSPRUCH, "ELW", "FW", "Lage unverändert")
        }
    }

    @Test
    fun reopen_addsDokumentationEntry_andAllowsWrites() = runTest {
        val einsatzDao = FakeEinsatzDao()
        val logDao = FakeLogEntryDao()
        val repo = repository(einsatzDao, logDao)

        val id = repo.createEinsatz("Übung")
        repo.closeEinsatz(id)
        repo.reopenEinsatz(id)

        assertEquals(1, logDao.items.value.size)
        assertEquals(MessageType.DOKUMENTATION, logDao.items.value.single().type)

        repo.addEntry(id, MessageType.LAGEMELDUNG, "FW", "ELW", "Feuer aus")
        assertEquals(2, logDao.items.value.size)
    }

    @Test
    fun createEinsatz_trimsName_andBlankMessageRejected() = runTest {
        val einsatzDao = FakeEinsatzDao()
        val logDao = FakeLogEntryDao()
        val repo = repository(einsatzDao, logDao)

        val id = repo.createEinsatz("  Übung Größenwahn  ")
        assertEquals("Übung Größenwahn", einsatzDao.items.value.single().name)
        assertTrue(einsatzDao.items.value.single().endedAtEpochMs == null)

        assertFailsWith<IllegalArgumentException> {
            repo.addEntry(id, MessageType.FUNKSPRUCH, null, null, "   ")
        }
    }
}
