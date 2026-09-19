package de.einsatzlog.app.core.text

/**
 * Case- and umlaut-insensitive matching (spec 002): "übung", "Übung" and
 * "ubung" all find "Übung Brandhaus".
 */
fun normalizeForSearch(text: String): String = buildString(text.length) {
    for (ch in text.lowercase()) {
        when (ch) {
            'ä' -> append('a')
            'ö' -> append('o')
            'ü' -> append('u')
            'ß' -> append("ss")
            else -> append(ch)
        }
    }
}

fun matchesSearch(haystack: String, query: String): Boolean =
    normalizeForSearch(haystack).contains(normalizeForSearch(query.trim()))
