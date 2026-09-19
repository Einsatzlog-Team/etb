package de.einsatzlog.app.core.export

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** A4 @ 72 dpi. */
private const val PAGE_WIDTH = 595
private const val PAGE_HEIGHT = 842
private const val MARGIN = 40f
private const val CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN

private const val COL_TIME = 46f
private const val COL_TYPE = 92f
private const val COL_FROMTO = 120f
private const val COL_GAP = 8f
private val COL_MESSAGE = CONTENT_WIDTH - COL_TIME - COL_TYPE - COL_FROMTO - 3 * COL_GAP

class AndroidLogbookExporter(private val context: Context) : LogbookExporter {

    override suspend fun exportPdf(document: PdfLogbookDocument): Result<Unit> = runCatching {
        val file = withContext(Dispatchers.IO) { render(document) }
        share(file)
    }

    private fun render(model: PdfLogbookDocument): File {
        val titlePaint = TextPaint().apply { textSize = 16f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true }
        val metaPaint = TextPaint().apply { textSize = 9f; color = 0xFF444444.toInt(); isAntiAlias = true }
        val cellPaint = TextPaint().apply { textSize = 9f; isAntiAlias = true }
        val cellBoldPaint = TextPaint().apply { textSize = 9f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true }
        val footerPaint = TextPaint().apply { textSize = 8f; color = 0xFF888888.toInt(); isAntiAlias = true }

        val pdf = PdfDocument()
        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var y = 0f

        fun newPage(): PdfDocument.Page {
            pageNumber += 1
            val p = pdf.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            )
            y = MARGIN
            return p
        }

        fun finishPage(p: PdfDocument.Page) {
            p.canvas.drawText(
                "${model.footer} – Seite $pageNumber",
                MARGIN,
                PAGE_HEIGHT - 20f,
                footerPaint,
            )
            pdf.finishPage(p)
        }

        fun layout(text: String, paint: TextPaint, width: Float): StaticLayout =
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .build()

        page = newPage()

        // Header
        page.canvas.apply {
            drawText(model.title, MARGIN, y + titlePaint.textSize, titlePaint)
            y += titlePaint.textSize + 10f
            model.metaLines.forEach { line ->
                drawText(line, MARGIN, y + metaPaint.textSize, metaPaint)
                y += metaPaint.textSize + 4f
            }
            y += 10f
        }

        // Rows
        model.rows.forEach { row ->
            val messageLayout = layout(row.message, cellPaint, COL_MESSAGE)
            val fromToLayout = layout(row.fromTo, cellPaint, COL_FROMTO)
            val rowHeight = maxOf(messageLayout.height.toFloat(), fromToLayout.height.toFloat(), 12f) + 6f

            if (y + rowHeight > PAGE_HEIGHT - 40f) {
                finishPage(page!!)
                page = newPage()
            }
            val canvas = page!!.canvas
            var x = MARGIN
            canvas.drawText(row.time, x, y + cellPaint.textSize, cellPaint)
            x += COL_TIME + COL_GAP
            canvas.drawText(row.typeLabel, x, y + cellBoldPaint.textSize, cellBoldPaint)
            x += COL_TYPE + COL_GAP
            canvas.save(); canvas.translate(x, y); fromToLayout.draw(canvas); canvas.restore()
            x += COL_FROMTO + COL_GAP
            canvas.save(); canvas.translate(x, y); messageLayout.draw(canvas); canvas.restore()
            y += rowHeight
        }

        finishPage(page!!)

        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, model.fileName + ".pdf")
        file.outputStream().use { pdf.writeTo(it) }
        pdf.close()
        return file
    }

    private fun share(file: File) {
        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
