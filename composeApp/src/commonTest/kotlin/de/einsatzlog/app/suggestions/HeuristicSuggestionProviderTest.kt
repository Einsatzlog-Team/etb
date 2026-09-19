package de.einsatzlog.app.suggestions

import de.einsatzlog.app.data.FakeLogEntryDao
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.domain.MessageType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class HeuristicSuggestionProviderTest {

    private var counter = 0

    private fun entry(
        einsatzId: String,
        source: String?,
        target: String?,
        createdAt: Long,
    ) = LogEntryEntity(
        id = "e-${counter++}",
        einsatzId = einsatzId,
        timestampEpochMs = createdAt,
        type = MessageType.FUNKSPRUCH,
        source = source,
        target = target,
        message = "m",
        createdAtEpochMs = createdAt,
    )

    @Test
    fun frequencyWins_withinSameRecency() = runTest {
        val dao = FakeLogEntryDao()
        dao.items.value = listOf(
            entry("a", "ELW", "FW", 1),
            entry("a", "ELW", "FW", 2),
            entry("a", "Florian 1", "FW", 3),
        )
        val provider = HeuristicSuggestionProvider(dao)

        assertEquals(listOf("ELW", "Florian 1"), provider.suggestSources("a"))
    }

    @Test
    fun currentEinsatzWeightedOverGlobal() = runTest {
        val dao = FakeLogEntryDao()
        dao.items.value = listOf(
            entry("other", "Leitstelle", null, 1),
            entry("other", "Leitstelle", null, 2),
            entry("current", "ELW", null, 3),
            entry("other", "Leitstelle", null, 4),
        )
        val provider = HeuristicSuggestionProvider(dao)

        // 1× in current Einsatz (2.0 weight) still loses to 3× global here,
        // but must rank ahead of a single global occurrence.
        val suggestions = provider.suggestSources("current")
        assertTrue(suggestions.indexOf("ELW") >= 0)
        assertEquals("Leitstelle", suggestions.first())

        dao.items.value = listOf(
            entry("other", "Leitstelle", null, 1),
            entry("current", "ELW", null, 2),
        )
        assertEquals("ELW", provider.suggestSources("current").first())
    }

    @Test
    fun targetSuggestions_boostVonZuPairs() = runTest {
        val dao = FakeLogEntryDao()
        dao.items.value = listOf(
            entry("a", "ELW", "Leitstelle", 1),
            entry("a", "Trupp 1", "Einsatzleiter", 2),
            entry("a", "Trupp 1", "Einsatzleiter", 3),
        )
        val provider = HeuristicSuggestionProvider(dao)

        // Without source context, frequency rules.
        assertEquals("Einsatzleiter", provider.suggestTargets("a", source = null).first())
        // With source=ELW, the co-occurring target ranks first despite lower frequency.
        assertEquals("Leitstelle", provider.suggestTargets("a", source = "ELW").first())
    }

    @Test
    fun blankValuesIgnored() = runTest {
        val dao = FakeLogEntryDao()
        dao.items.value = listOf(entry("a", null, "", 1), entry("a", "ELW", null, 2))
        val provider = HeuristicSuggestionProvider(dao)

        assertEquals(listOf("ELW"), provider.suggestSources("a"))
        assertTrue(provider.suggestTargets("a", null).isEmpty())
    }
}
