package de.einsatzlog.app.data

import de.einsatzlog.app.data.db.EinsatzDao
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.db.EinsatzEntryCount
import de.einsatzlog.app.data.db.LogEntryDao
import de.einsatzlog.app.data.db.LogEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeEinsatzDao : EinsatzDao {
    val items = MutableStateFlow<List<EinsatzEntity>>(emptyList())

    override fun observeAll(): Flow<List<EinsatzEntity>> = items
    override fun observeById(id: String): Flow<EinsatzEntity?> = items.map { l -> l.find { it.id == id } }
    override suspend fun getById(id: String): EinsatzEntity? = items.value.find { it.id == id }
    override suspend fun insert(einsatz: EinsatzEntity) { items.value += einsatz }
    override suspend fun update(einsatz: EinsatzEntity) {
        items.value = items.value.map { if (it.id == einsatz.id) einsatz else it }
    }
    override suspend fun deleteById(id: String) { items.value = items.value.filterNot { it.id == id } }
}

class FakeLogEntryDao : LogEntryDao {
    val items = MutableStateFlow<List<LogEntryEntity>>(emptyList())

    override fun observeForEinsatz(einsatzId: String): Flow<List<LogEntryEntity>> =
        items.map { l -> l.filter { it.einsatzId == einsatzId } }
    override fun observeEntryCounts(): Flow<List<EinsatzEntryCount>> =
        items.map { l ->
            l.groupBy { it.einsatzId }.map { (id, entries) -> EinsatzEntryCount(id, entries.size) }
        }
    override fun observeCount(einsatzId: String): Flow<Int> =
        items.map { l -> l.count { it.einsatzId == einsatzId } }
    override suspend fun insert(entry: LogEntryEntity) { items.value += entry }
    override suspend fun deleteById(id: String) { items.value = items.value.filterNot { it.id == id } }
    override suspend fun recentEntries(limit: Int): List<LogEntryEntity> =
        items.value.sortedByDescending { it.createdAtEpochMs }.take(limit)
}
