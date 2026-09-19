package de.einsatzlog.app.core.export

import de.einsatzlog.app.core.format.formatDateTime
import de.einsatzlog.app.core.format.formatTime
import de.einsatzlog.app.data.db.EinsatzEntity
import de.einsatzlog.app.data.db.LogEntryEntity

data class PdfLogRow(
    val time: String,
    val typeLabel: String,
    val fromTo: String,
    val message: String,
)

data class PdfLogbookDocument(
    val title: String,
    val metaLines: List<String>,
    val rows: List<PdfLogRow>,
    val footer: String,
    /** Sanitized file name without extension. */
    val fileName: String,
)

/** Pure-Kotlin document builder (spec 005) — platform renderers only draw. */
fun buildPdfDocument(
    einsatz: EinsatzEntity,
    entries: List<LogEntryEntity>,
    generatedAtEpochMs: Long,
): PdfLogbookDocument {
    val metaLines = buildList {
        add("Beginn: " + formatDateTime(einsatz.startedAtEpochMs))
        einsatz.endedAtEpochMs?.let { add("Ende: " + formatDateTime(it)) }
        einsatz.description?.let { add(it) }
        add("${entries.size} Einträge")
    }
    val rows = entries
        .sortedWith(compareBy({ it.timestampEpochMs }, { it.createdAtEpochMs }))
        .map { entry ->
            PdfLogRow(
                time = formatTime(entry.timestampEpochMs),
                typeLabel = entry.type.labelDe,
                fromTo = listOfNotNull(entry.source, entry.target).joinToString(" → "),
                message = entry.message,
            )
        }
    return PdfLogbookDocument(
        title = "Einsatztagebuch: ${einsatz.name}",
        metaLines = metaLines,
        rows = rows,
        footer = "Erstellt mit Einsatzlog, " + formatDateTime(generatedAtEpochMs),
        fileName = "Einsatztagebuch_" + sanitizeFileName(einsatz.name),
    )
}

internal fun sanitizeFileName(name: String): String {
    val cleaned = name
        .map { c -> if (c.isLetterOrDigit() || c == '-' || c == '_') c else '_' }
        .joinToString("")
        .take(60)
    return if (cleaned.any { it.isLetterOrDigit() }) cleaned else "Einsatz"
}
