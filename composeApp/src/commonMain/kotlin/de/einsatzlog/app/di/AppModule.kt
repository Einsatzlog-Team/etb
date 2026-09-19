package de.einsatzlog.app.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.einsatzlog.app.core.CrashReporter
import de.einsatzlog.app.core.NoopCrashReporter
import de.einsatzlog.app.data.EinsatzRepository
import de.einsatzlog.app.data.db.AppDatabase
import de.einsatzlog.app.suggestions.HeuristicSuggestionProvider
import de.einsatzlog.app.suggestions.SuggestionProvider
import de.einsatzlog.app.support.NoopSupportRepository
import de.einsatzlog.app.support.RevenueCatConfig
import de.einsatzlog.app.support.RevenueCatSupportRepository
import de.einsatzlog.app.support.SupportRepository
import de.einsatzlog.app.ui.entry.EntryFormViewModel
import de.einsatzlog.app.ui.home.HomeViewModel
import de.einsatzlog.app.ui.logbook.LogbookViewModel
import de.einsatzlog.app.ui.support.SupportViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Platform contributes the Room builder (Android needs a Context, iOS a file path). */
expect val platformModule: Module

/**
 * @param purchasesEnabled false for the foss flavor — RevenueCat is then
 * never selected nor initialized (spec 004).
 */
@OptIn(ExperimentalUuidApi::class)
fun appModule(purchasesEnabled: Boolean) = module {
    single<AppDatabase> {
        get<RoomDatabase.Builder<AppDatabase>>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single { get<AppDatabase>().einsatzDao() }
    single { get<AppDatabase>().logEntryDao() }

    single { EinsatzRepository(einsatzDao = get(), logEntryDao = get(), newId = { Uuid.random().toString() }) }

    single<CrashReporter> { NoopCrashReporter }
    single<SupportRepository> {
        if (purchasesEnabled && RevenueCatConfig.isConfigured) RevenueCatSupportRepository()
        else NoopSupportRepository()
    }
    single<SuggestionProvider> { HeuristicSuggestionProvider(logEntryDao = get()) }

    viewModel { HomeViewModel(repository = get()) }
    viewModel { (einsatzId: String) ->
        LogbookViewModel(einsatzId = einsatzId, repository = get(), exporter = get())
    }
    viewModel { (einsatzId: String) ->
        EntryFormViewModel(einsatzId = einsatzId, repository = get(), suggestions = get())
    }
    viewModel { SupportViewModel(repository = get()) }
}
