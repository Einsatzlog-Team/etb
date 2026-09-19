package de.einsatzlog.app.suggestions

import de.einsatzlog.app.domain.MessageType

/**
 * Seam for Von/Zu/message suggestions (spec 003). v1 ships a plain-Kotlin
 * recency+frequency heuristic (slice 2); the on-device AI implementation
 * (SKaiNET, `store` variant) plugs in here later without touching feature code.
 */
interface SuggestionProvider {
    suspend fun suggestSources(einsatzId: String): List<String>
    suspend fun suggestTargets(einsatzId: String, source: String?): List<String>
    suspend fun suggestTemplates(type: MessageType): List<String>
}

object NoSuggestions : SuggestionProvider {
    override suspend fun suggestSources(einsatzId: String): List<String> = emptyList()
    override suspend fun suggestTargets(einsatzId: String, source: String?): List<String> = emptyList()
    override suspend fun suggestTemplates(type: MessageType): List<String> = emptyList()
}
