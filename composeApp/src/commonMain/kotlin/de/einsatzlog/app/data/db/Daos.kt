package de.einsatzlog.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EinsatzDao {

    /** Active Einsätze first, then newest start. */
    @Query(
        """
        SELECT * FROM einsatz
        ORDER BY CASE WHEN endedAtEpochMs IS NULL THEN 0 ELSE 1 END, startedAtEpochMs DESC
        """
    )
    fun observeAll(): Flow<List<EinsatzEntity>>

    @Query("SELECT * FROM einsatz WHERE id = :id")
    fun observeById(id: String): Flow<EinsatzEntity?>

    @Query("SELECT * FROM einsatz WHERE id = :id")
    suspend fun getById(id: String): EinsatzEntity?

    @Insert
    suspend fun insert(einsatz: EinsatzEntity)

    @Update
    suspend fun update(einsatz: EinsatzEntity)

    @Query("DELETE FROM einsatz WHERE id = :id")
    suspend fun deleteById(id: String)
}

data class EinsatzEntryCount(val einsatzId: String, val count: Int)

@Dao
interface LogEntryDao {

    @Query(
        "SELECT * FROM log_entry WHERE einsatzId = :einsatzId ORDER BY timestampEpochMs ASC, createdAtEpochMs ASC"
    )
    fun observeForEinsatz(einsatzId: String): Flow<List<LogEntryEntity>>

    @Query("SELECT einsatzId, COUNT(*) AS count FROM log_entry GROUP BY einsatzId")
    fun observeEntryCounts(): Flow<List<EinsatzEntryCount>>

    /** Newest first — input for suggestion ranking (spec 003). */
    @Query("SELECT * FROM log_entry ORDER BY createdAtEpochMs DESC LIMIT :limit")
    suspend fun recentEntries(limit: Int): List<LogEntryEntity>

    @Query("SELECT COUNT(*) FROM log_entry WHERE einsatzId = :einsatzId")
    fun observeCount(einsatzId: String): Flow<Int>

    @Insert
    suspend fun insert(entry: LogEntryEntity)

    @Query("DELETE FROM log_entry WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle WHERE einsatzId = :einsatzId ORDER BY addedAtEpochMs ASC")
    fun observeForEinsatz(einsatzId: String): Flow<List<VehicleEntity>>

    @Insert
    suspend fun insert(vehicle: VehicleEntity)
}

@Dao
interface ResourceEntryDao {
    @Query("SELECT * FROM resource_entry WHERE einsatzId = :einsatzId ORDER BY timestampEpochMs ASC")
    fun observeForEinsatz(einsatzId: String): Flow<List<ResourceEntryEntity>>

    @Insert
    suspend fun insert(entry: ResourceEntryEntity)
}
