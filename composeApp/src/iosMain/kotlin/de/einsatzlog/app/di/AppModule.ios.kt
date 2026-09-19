package de.einsatzlog.app.di

import androidx.room.Room
import androidx.room.RoomDatabase
import de.einsatzlog.app.core.export.IosLogbookExporter
import de.einsatzlog.app.core.export.LogbookExporter
import de.einsatzlog.app.data.db.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformModule: Module = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        Room.databaseBuilder<AppDatabase>(
            name = documentDirectory() + "/" + AppDatabase.FILE_NAME,
        )
    }
    single<LogbookExporter> { IosLogbookExporter() }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(documentDirectory?.path) { "Unable to resolve iOS documents directory" }
}
