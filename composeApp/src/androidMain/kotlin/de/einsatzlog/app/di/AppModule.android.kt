package de.einsatzlog.app.di

import androidx.room.Room
import androidx.room.RoomDatabase
import de.einsatzlog.app.core.export.AndroidLogbookExporter
import de.einsatzlog.app.core.export.LogbookExporter
import de.einsatzlog.app.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val context = androidContext()
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = context.getDatabasePath(AppDatabase.FILE_NAME).absolutePath,
        )
    }
    single<LogbookExporter> { AndroidLogbookExporter(androidContext()) }
}
