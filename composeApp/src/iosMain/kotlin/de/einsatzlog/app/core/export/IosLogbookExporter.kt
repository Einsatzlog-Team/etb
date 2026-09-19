package de.einsatzlog.app.core.export

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.UIKit.NSFontAttributeName
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIGraphicsBeginPDFContextToFile
import platform.UIKit.UIGraphicsBeginPDFPageWithInfo
import platform.UIKit.UIGraphicsEndPDFContext
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.boundingRectWithSize
import platform.UIKit.drawInRect
import platform.UIKit.NSStringDrawingUsesLineFragmentOrigin

/** A4 in points. */
private const val PAGE_WIDTH = 595.0
private const val PAGE_HEIGHT = 842.0
private const val MARGIN = 40.0
private const val CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN
private const val COL_TIME = 46.0
private const val COL_TYPE = 92.0
private const val COL_FROMTO = 120.0
private const val COL_GAP = 8.0
private const val COL_MESSAGE = CONTENT_WIDTH - COL_TIME - COL_TYPE - COL_FROMTO - 3 * COL_GAP

/**
 * iOS renderer (spec 005): CoreGraphics PDF context + NSString drawing,
 * then UIActivityViewController (share/print/save to Files).
 * NOTE: cannot be compiled/verified on the Linux dev box — first run on the Mac.
 */
@OptIn(ExperimentalForeignApi::class)
class IosLogbookExporter : LogbookExporter {

    override suspend fun exportPdf(document: PdfLogbookDocument): Result<Unit> = runCatching {
        val path = NSTemporaryDirectory() + document.fileName + ".pdf"
        render(document, path)
        withContext(Dispatchers.Main) { presentShareSheet(path) }
    }

    private fun attrs(size: Double, bold: Boolean = false, gray: Boolean = false): Map<Any?, *> = mapOf(
        NSFontAttributeName to if (bold) UIFont.boldSystemFontOfSize(size) else UIFont.systemFontOfSize(size),
        NSForegroundColorAttributeName to if (gray) UIColor.grayColor else UIColor.blackColor,
    )

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun textHeight(text: String, size: Double, width: Double): Double {
        val ns = text as NSString
        val rect = ns.boundingRectWithSize(
            size = CGSizeMake(width, 10_000.0),
            options = NSStringDrawingUsesLineFragmentOrigin,
            attributes = attrs(size) as Map<Any?, *>,
            context = null,
        )
        return rect.useContents { this.size.height }
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun draw(text: String, x: Double, y: Double, width: Double, height: Double, size: Double, bold: Boolean = false, gray: Boolean = false) {
        (text as NSString).drawInRect(
            rect = CGRectMake(x, y, width, height),
            withAttributes = attrs(size, bold, gray) as Map<Any?, *>,
        )
    }

    private fun render(model: PdfLogbookDocument, path: String) {
        val pageRect: CValue<CGRect> = CGRectMake(0.0, 0.0, PAGE_WIDTH, PAGE_HEIGHT)
        UIGraphicsBeginPDFContextToFile(path, pageRect, null)
        var pageNumber = 0
        var y = 0.0

        fun newPage() {
            pageNumber += 1
            UIGraphicsBeginPDFPageWithInfo(pageRect, null)
            y = MARGIN
        }

        fun footer() {
            draw("${model.footer} – Seite $pageNumber", MARGIN, PAGE_HEIGHT - 28.0, CONTENT_WIDTH, 14.0, 8.0, gray = true)
        }

        newPage()
        draw(model.title, MARGIN, y, CONTENT_WIDTH, 22.0, 16.0, bold = true)
        y += 28.0
        model.metaLines.forEach { line ->
            draw(line, MARGIN, y, CONTENT_WIDTH, 14.0, 9.0, gray = true)
            y += 13.0
        }
        y += 10.0

        model.rows.forEach { row ->
            val messageHeight = textHeight(row.message, 9.0, COL_MESSAGE)
            val fromToHeight = textHeight(row.fromTo, 9.0, COL_FROMTO)
            val rowHeight = maxOf(messageHeight, fromToHeight, 12.0) + 6.0

            if (y + rowHeight > PAGE_HEIGHT - 44.0) {
                footer()
                newPage()
            }
            var x = MARGIN
            draw(row.time, x, y, COL_TIME, 14.0, 9.0)
            x += COL_TIME + COL_GAP
            draw(row.typeLabel, x, y, COL_TYPE, 14.0, 9.0, bold = true)
            x += COL_TYPE + COL_GAP
            draw(row.fromTo, x, y, COL_FROMTO, fromToHeight + 2, 9.0)
            x += COL_FROMTO + COL_GAP
            draw(row.message, x, y, COL_MESSAGE, messageHeight + 2, 9.0)
            y += rowHeight
        }
        footer()
        UIGraphicsEndPDFContext()
    }

    private fun presentShareSheet(path: String) {
        val url = NSURL.fileURLWithPath(path)
        val controller = UIActivityViewController(activityItems = listOf(url), applicationActivities = null)
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController
        root?.presentViewController(controller, animated = true, completion = null)
    }
}
