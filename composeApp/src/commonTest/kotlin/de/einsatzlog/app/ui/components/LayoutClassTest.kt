package de.einsatzlog.app.ui.components

import de.einsatzlog.app.ui.components.templates.LayoutClass
import de.einsatzlog.app.ui.components.templates.layoutClassForWidth
import kotlin.test.Test
import kotlin.test.assertEquals

class LayoutClassTest {
    @Test
    fun phonesStayCompact() {
        assertEquals(LayoutClass.COMPACT, layoutClassForWidth(360f))
        assertEquals(LayoutClass.COMPACT, layoutClassForWidth(599f))
    }

    @Test
    fun smallTabletsAndPortraitAreMedium() {
        assertEquals(LayoutClass.MEDIUM, layoutClassForWidth(600f))
        assertEquals(LayoutClass.MEDIUM, layoutClassForWidth(839f))
    }

    @Test
    fun tenInchLandscapeIsExpanded() {
        assertEquals(LayoutClass.EXPANDED, layoutClassForWidth(840f))
        assertEquals(LayoutClass.EXPANDED, layoutClassForWidth(1280f))
    }
}
