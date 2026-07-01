package com.tttsaurus.ksml.language.editor.renderer

import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import com.tttsaurus.ksml.language.VisualPrefabs
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle

class BadgeRenderer(
    private val text: String
) : EditorCustomElementRenderer {

    private val OUTER_PADDING_X = 6
    private val INNER_PADDING_X = 6

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val editor = inlay.editor
        val scheme = editor.colorsScheme
        val font = scheme.getFont(EditorFontType.PLAIN)
        val metrics = editor.contentComponent.getFontMetrics(font)

        val textWidth = metrics.stringWidth(text)

        return OUTER_PADDING_X * 2 +
                INNER_PADDING_X * 2 +
                textWidth
    }

    override fun paint(
        inlay: Inlay<*>,
        g: Graphics,
        targetRegion: Rectangle,
        textAttributes: TextAttributes
    ) {
        val g2 = g as Graphics2D
        val editor = inlay.editor
        val scheme = editor.colorsScheme
        val font = scheme.getFont(EditorFontType.PLAIN)

        g2.font = font
        val metrics = g2.fontMetrics
        val cornerRadius = (0.45f * metrics.height).toInt()

        g2.color = VisualPrefabs.MODULE_IMPORT_SPECIAL_RENDER_BADGE_COLOR
        g2.fillRoundRect(
            targetRegion.x + OUTER_PADDING_X,
            targetRegion.y + 2,
            targetRegion.width - OUTER_PADDING_X * 2,
            targetRegion.height - 4,
            cornerRadius,
            cornerRadius
        )

        val baseline = targetRegion.y +
                (targetRegion.height - metrics.height) / 2 +
                metrics.ascent

        g2.color = VisualPrefabs.MODULE_IMPORT_SPECIAL_RENDER_BADGE_TEXT_COLOR
        g2.drawString(
            text,
            targetRegion.x + OUTER_PADDING_X + INNER_PADDING_X,
            baseline
        )
    }
}
