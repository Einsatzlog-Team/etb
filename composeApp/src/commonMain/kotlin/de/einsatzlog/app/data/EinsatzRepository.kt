package de.einsatzlog.app.data

import de.einsatzlog.app.data.db.EinsatzDao
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.db.LogEntryDao
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.domain.MessageType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Thrown when writing to a closed (read-only) Einsatz — the record is sacred. */
class EinsatzClosedException(einsatzId: String) :
    IllegalStateException("Einsatz $einsatzId is closed and read-only")

@OptIn(ExperimentalTime::class)
class EinsatzRepository(
    private val einsatzDao: EinsatzDao,
    private val logEntryDao: LogEntryDao,
    private val newId: () -> String,
    private val nowEpochMs: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {

    fun observeEinsaetze(): Flow<List<EinsatzEntity>> = einsatzDao.observeAll()

    fun observeEinsatz(id: String): Flow<EinsatzEntity?> = einsatzDao.observeById(id)

    fun observeEntries(einsatzId: String): Flow<List<LogEntryEntity>> =
        logEntryDao.observeForEinsatz(einsatzId)

    fun observeEntryCount(einsatzId: String): Flow<Int> = logEntryDao.observeCount(einsatzId)

    fun observeEntryCounts(): Flow<Map<String, Int>> =
        logEntryDao.observeEntryCounts().map { list -> list.associate { it.einsatzId to it.count } }

    suspend fun createEinsatz(
        name: String,
        description: String? = null,
        startedAtEpochMs: Long = nowEpochMs(),
    ): String {
        val id = newId()
        einsatzDao.insert(
            EinsatzEntity(
                id = id,
                name = name.trim(),
                description = description?.trim()?.takeIf { it.isNotEmpty() },
                startedAtEpochMs = startedAtEpochMs,
                endedAtEpochMs = null,
                createdAtEpochMs = nowEpochMs(),
            )
        )
        return id
    }

    suspend fun addEntry(
        einsatzId: String,
        type: MessageType,
        source: String?,
        target: String?,
        message: String,
        timestampEpochMs: Long = nowEpochMs(),
    ): String {
        val einsatz = einsatzDao.getById(einsatzId)
            ?: throw IllegalArgumentException("Unknown Einsatz $einsatzId")
        if (einsatz.endedAtEpochMs != null) throw EinsatzClosedException(einsatzId)
        require(message.isNotBlank()) { "Message must not be blank" }
        val id = newId()
        logEntryDao.insert(
            LogEntryEntity(
                id = id,
                einsatzId = einsatzId,
                timestampEpochMs = timestampEpochMs,
                type = type,
                source = source?.trim()?.takeIf { it.isNotEmpty() },
                target = target?.trim()?.takeIf { it.isNotEmpty() },
                message = message.trim(),
                createdAtEpochMs = nowEpochMs(),
            )
        )
        return id
    }

    suspend fun closeEinsatz(einsatzId: String) {
        val einsatz = einsatzDao.getById(einsatzId) ?: return
        if (einsatz.endedAtEpochMs != null) return
        einsatzDao.update(einsatz.copy(endedAtEpochMs = nowEpochMs()))
    }

    /** Reopening is allowed but auto-documented (spec 002). */
    suspend fun reopenEinsatz(einsatzId: String) {
        val einsatz = einsatzDao.getById(einsatzId) ?: return
        if (einsatz.endedAtEpochMs == null) return
        einsatzDao.update(einsatz.copy(endedAtEpochMs = null))
        addEntry(
            einsatzId = einsatzId,
            type = MessageType.DOKUMENTATION,
            source = null,
            target = null,
            message = "Einsatz wieder geöffnet",
        )
    }

    suspend fun deleteEinsatz(einsatzId: String) = einsatzDao.deleteById(einsatzId)
}
