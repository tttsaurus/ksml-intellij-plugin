package com.tttsaurus.ksml.language.editor.renderer

import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle

class BadgeRenderer(
    private val text: String
) : EditorCustomElementRenderer {

    override fun calcWidthInPixels(inlay: Inlay<*>): Int =
        8 + text.length * 7 + 10

    override fun paint(
        inlay: Inlay<*>,
        g: Graphics,
        targetRegion: Rectangle,
        textAttributes: TextAttributes
    ) {
        val g2 = g as Graphics2D

        g2.color = Color(255, 136, 64, 200)
        g2.fillRoundRect(
            targetRegion.x,
            targetRegion.y + 2,
            targetRegion.width - 5,
            targetRegion.height - 4,
            8,
            8
        )

        g2.color = Color.WHITE
        g2.drawString(
            text,
            targetRegion.x + 4,
            targetRegion.y + targetRegion.height - 6
        )
    }
}
