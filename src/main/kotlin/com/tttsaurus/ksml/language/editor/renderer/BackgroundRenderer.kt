package com.tttsaurus.ksml.language.editor.renderer

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.tttsaurus.ksml.language.VisualPrefabs
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle

class BackgroundRenderer : CustomHighlighterRenderer {

    override fun paint(
        editor: Editor,
        highlighter: RangeHighlighter,
        g: Graphics
    ) {
        val start = highlighter.startOffset
        val end = highlighter.endOffset

        val startXY = editor.offsetToXY(start)
        val endXY = editor.offsetToXY(end)

        val lineHeight = editor.lineHeight

        val rect = Rectangle(
            startXY.x,
            startXY.y,
            endXY.x - startXY.x,
            lineHeight
        )

        val g2 = g as Graphics2D
        val cornerRadius = (0.45f * lineHeight).toInt()

        g2.color = VisualPrefabs.MODULE_IMPORT_SPECIAL_RENDER_MODULE_TEXT_BG_COLOR
        g2.fillRoundRect(
            rect.x,
            rect.y + 1,
            rect.width,
            rect.height - 2,
            cornerRadius,
            cornerRadius
        )
    }
}
