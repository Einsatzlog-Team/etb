package de.einsatzlog.app.suggestions

import de.einsatzlog.app.data.db.LogEntryDao
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.domain.MessageType

/**
 * v1 suggestion heuristic (spec 003): recency + frequency ranking over the
 * last [historyLimit] entries, with the current Einsatz weighted double and
 * Von→Zu co-occurrence boosting targets for a chosen source. Pure Kotlin —
 * the on-device AI implementation replaces this behind the same interface.
 */
class HeuristicSuggestionProvider(
    private val logEntryDao: LogEntryDao,
    private val historyLimit: Int = 200,
    private val maxSuggestions: Int = 5,
) : SuggestionProvider {

    override suspend fun suggestSources(einsatzId: String): List<String> =
        rank(einsatzId) { it.source }

    override suspend fun suggestTargets(einsatzId: String, source: String?): List<String> {
        val entries = logEntryDao.recentEntries(historyLimit)
        val scores = mutableMapOf<String, Double>()
        entries.forEachIndexed { index, entry ->
            val value = entry.target?.takeIf { it.isNotBlank() } ?: return@forEachIndexed
            var weight = recencyWeight(index) * einsatzWeight(entry, einsatzId)
            // Von→Zu pairs: targets seen together with this source rank first.
            if (source != null && entry.source.equals(source, ignoreCase = true)) weight *= 3.0
            scores[value] = (scores[value] ?: 0.0) + weight
        }
        return topRanked(scores)
    }

    override suspend fun suggestTemplates(type: MessageType): List<String> = emptyList() // fast-follow

    private suspend fun rank(einsatzId: String, extract: (LogEntryEntity) -> String?): List<String> {
        val entries = logEntryDao.recentEntries(historyLimit)
        val scores = mutableMapOf<String, Double>()
        entries.forEachIndexed { index, entry ->
            val value = extract(entry)?.takeIf { it.isNotBlank() } ?: return@forEachIndexed
            val weight = recencyWeight(index) * einsatzWeight(entry, einsatzId)
            scores[value] = (scores[value] ?: 0.0) + weight
        }
        return topRanked(scores)
    }

    private fun recencyWeight(indexNewestFirst: Int): Double = kotlin.math.exp(-indexNewestFirst / 40.0)

    private fun einsatzWeight(entry: LogEntryEntity, einsatzId: String): Double =
        if (entry.einsatzId == einsatzId) 2.0 else 1.0

    private fun topRanked(scores: Map<String, Double>): List<String> =
        scores.entries.sortedByDescending { it.value }.take(maxSuggestions).map { it.key }
}
