package de.einsatzlog.app.core.export

import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.db.LogEntryEntity
import de.einsatzlog.app.domain.MessageType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PdfExportModelTest {

    private val einsatz = EinsatzEntity(
        id = "e1",
        name = "Übung Brandhaus",
        description = "Jahresübung",
        startedAtEpochMs = 1_700_000_000_000,
        endedAtEpochMs = null,
        createdAtEpochMs = 1_700_000_000_000,
    )

    private fun entry(id: String, ts: Long, message: String, source: String? = "ELW", target: String? = "FW") =
        LogEntryEntity(
            id = id, einsatzId = "e1", timestampEpochMs = ts, type = MessageType.FUNKSPRUCH,
            source = source, target = target, message = message, createdAtEpochMs = ts,
        )

    @Test
    fun rowsChronological_regardlessOfInputOrder() {
        val doc = buildPdfDocument(
            einsatz,
            entries = listOf(
                entry("b", 2_000, "zweite"),
                entry("a", 1_000, "erste"),
            ),
            generatedAtEpochMs = 3_000,
        )
        assertEquals(listOf("erste", "zweite"), doc.rows.map { it.message })
    }

    @Test
    fun metaAndTitleAndFooter() {
        val doc = buildPdfDocument(einsatz, listOf(entry("a", 1_000, "m")), generatedAtEpochMs = 2_000)
        assertEquals("Einsatztagebuch: Übung Brandhaus", doc.title)
        assertTrue(doc.metaLines.any { it.startsWith("Beginn:") })
        assertTrue(doc.metaLines.contains("Jahresübung"))
        assertTrue(doc.metaLines.contains("1 Einträge"))
        assertTrue(doc.footer.startsWith("Erstellt mit Einsatzlog"))
        assertTrue(doc.metaLines.none { it.startsWith("Ende:") }, "open Einsatz has no end line")
    }

    @Test
    fun fromToAndFileName() {
        val doc = buildPdfDocument(einsatz, listOf(entry("a", 1_000, "m", source = "ELW", target = null)), 2_000)
        assertEquals("ELW", doc.rows.single().fromTo)
        assertEquals("Einsatztagebuch_Übung_Brandhaus", doc.fileName)
        assertEquals("Einsatz", sanitizeFileName("///"))
    }
}
