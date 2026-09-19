package de.einsatzlog.app.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [
        EinsatzEntity::class,
        LogEntryEntity::class,
        VehicleEntity::class,
        ResourceEntryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun einsatzDao(): EinsatzDao
    abstract fun logEntryDao(): LogEntryDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun resourceEntryDao(): ResourceEntryDao

    companion object {
        const val FILE_NAME = "einsatzlog.db"
    }
}

// The Room compiler generates the `actual` implementations per target.
@Suppress("KotlinNoActualForExpect", "NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
