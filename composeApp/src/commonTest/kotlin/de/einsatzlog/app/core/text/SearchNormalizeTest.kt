package de.einsatzlog.app.core.text

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchNormalizeTest {

    @Test
    fun caseInsensitive() {
        assertTrue(matchesSearch("Übung Brandhaus", "übung"))
        assertTrue(matchesSearch("Übung Brandhaus", "BRANDHAUS"))
    }

    @Test
    fun umlautInsensitive() {
        assertTrue(matchesSearch("Übung Brandhaus", "ubung"))
        assertTrue(matchesSearch("Einsatz Größenwahn", "grossenwahn"))
        assertTrue(matchesSearch("Ölspur B3", "olspur"))
    }

    @Test
    fun trimsQueryAndRejectsNonMatches() {
        assertTrue(matchesSearch("Zimmerbrand", "  zimmer "))
        assertFalse(matchesSearch("Zimmerbrand", "flaeche"))
    }
}
