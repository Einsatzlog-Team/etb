package de.einsatzlog.app.ui.home

import de.einsatzlog.app.data.EinsatzRepository
import de.einsatzlog.app.data.FakeEinsatzDao
import de.einsatzlog.app.data.FakeLogEntryDao
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun build(): Triple<HomeViewModel, FakeEinsatzDao, EinsatzRepository> {
        val einsatzDao = FakeEinsatzDao()
        val logDao = FakeLogEntryDao()
        var counter = 0
        val repo = EinsatzRepository(einsatzDao, logDao, newId = { "id-${counter++}" })
        return Triple(HomeViewModel(repo, undoWindowMs = 12_000), einsatzDao, repo)
    }

    @Test
    fun requestDelete_hidesImmediately_undoRestores() = runTest(dispatcher.scheduler) {
        val (vm, dao, repo) = build()
        repo.createEinsatz("Übung")
        runCurrent()

        val id = dao.items.value.single().id
        vm.requestDelete(id)
        runCurrent()

        assertTrue(vm.uiState.first { it.loaded }.einsaetze.isEmpty(), "hidden during undo window")
        assertEquals(1, dao.items.value.size, "not yet deleted from DB")

        vm.undoDelete()
        runCurrent()
        assertEquals(1, vm.uiState.first { it.loaded }.einsaetze.size, "restored after undo")

        advanceTimeBy(20_000)
        runCurrent()
        assertEquals(1, dao.items.value.size, "undo cancelled the deletion permanently")
    }

    @Test
    fun requestDelete_commitsAfterWindow() = runTest(dispatcher.scheduler) {
        val (vm, dao, repo) = build()
        repo.createEinsatz("Übung")
        runCurrent()
        backgroundScope.launch { vm.uiState.collect {} }

        vm.requestDelete(dao.items.value.single().id)
        advanceTimeBy(13_000)
        runCurrent()

        assertTrue(dao.items.value.isEmpty(), "deleted after undo window elapsed")
    }

    @Test
    fun search_filtersUmlautInsensitive() = runTest(dispatcher.scheduler) {
        val (vm, _, repo) = build()
        repo.createEinsatz("Übung Brandhaus")
        repo.createEinsatz("Ölspur B3")
        runCurrent()

        vm.onQueryChange("ubung")
        val state = vm.uiState.first { it.loaded && it.einsaetze.size == 1 }
        assertEquals("Übung Brandhaus", state.einsaetze.single().name)
    }
}
