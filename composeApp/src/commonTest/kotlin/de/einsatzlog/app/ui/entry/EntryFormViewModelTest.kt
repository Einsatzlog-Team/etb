package de.einsatzlog.app.ui.entry

import de.einsatzlog.app.data.EinsatzRepository
import de.einsatzlog.app.data.FakeEinsatzDao
import de.einsatzlog.app.data.FakeLogEntryDao
import de.einsatzlog.app.domain.MessageType
import de.einsatzlog.app.suggestions.HeuristicSuggestionProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class EntryFormViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private suspend fun setup(now: Long = 1_000_000L): Pair<EntryFormViewModel, FakeLogEntryDao> {
        val einsatzDao = FakeEinsatzDao()
        val logDao = FakeLogEntryDao()
        var counter = 0
        val repo = EinsatzRepository(einsatzDao, logDao, newId = { "id-${counter++}" }, nowEpochMs = { now })
        val einsatzId = repo.createEinsatz("Übung")
        val vm = EntryFormViewModel(
            einsatzId = einsatzId,
            repository = repo,
            suggestions = HeuristicSuggestionProvider(logDao),
            nowEpochMs = { now },
        )
        return vm to logDao
    }

    @Test
    fun timestampIsCaptureTime_backdateAndResetWork() = runTest(dispatcher.scheduler) {
        val (vm, _) = setup(now = 1_000_000L)
        runCurrent()

        assertEquals(1_000_000L, vm.state.value.timestampEpochMs, "timestamp = form-open time")

        vm.backdate(5)
        vm.backdate(1)
        assertEquals(1_000_000L - 6 * 60_000, vm.state.value.timestampEpochMs)

        vm.resetTimestamp()
        assertEquals(1_000_000L, vm.state.value.timestampEpochMs)
    }

    @Test
    fun submitRequiresMessage_andWritesEntry() = runTest(dispatcher.scheduler) {
        val (vm, logDao) = setup()
        runCurrent()

        assertFalse(vm.state.value.canSubmit, "blank message must not be submittable")

        vm.onTypeChange(MessageType.LAGEMELDUNG)
        vm.onSourceChange("ELW")
        vm.onTargetChange("Leitstelle")
        vm.onMessageChange("Feuer unter Kontrolle")
        assertTrue(vm.state.value.canSubmit)

        vm.submit()
        runCurrent()

        val entry = logDao.items.value.single()
        assertEquals(MessageType.LAGEMELDUNG, entry.type)
        assertEquals("ELW", entry.source)
        assertEquals("Leitstelle", entry.target)
        assertEquals("Feuer unter Kontrolle", entry.message)
    }

    @Test
    fun suggestionsLoadFromHistory() = runTest(dispatcher.scheduler) {
        val einsatzDao = FakeEinsatzDao()
        val logDao = FakeLogEntryDao()
        var counter = 0
        val repo = EinsatzRepository(einsatzDao, logDao, newId = { "id-${counter++}" })
        val einsatzId = repo.createEinsatz("Übung")
        repo.addEntry(einsatzId, MessageType.FUNKSPRUCH, "ELW", "Leitstelle", "erste Meldung")

        val vm = EntryFormViewModel(einsatzId, repo, HeuristicSuggestionProvider(logDao))
        runCurrent()

        assertEquals(listOf("ELW"), vm.state.value.sourceSuggestions)
        assertEquals(listOf("Leitstelle"), vm.state.value.targetSuggestions)
    }
}
