package de.einsatzlog.app.core.export

/**
 * Renders a logbook document to PDF and opens the platform share sheet
 * (spec 005). Platform implementations are bound via Koin.
 */
interface LogbookExporter {
    suspend fun exportPdf(document: PdfLogbookDocument): Result<Unit>
}
