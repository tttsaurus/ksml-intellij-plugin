package com.tttsaurus.ksml.language

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

object VisualPrefabs {

    @Suppress("Deprecation")
    val MODULE_FUNC_CALL_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
        "GLSL_MODULE_REF_HIGHLIGHT",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(123, 163, 62), Color(123, 163, 62))
            fontType = Font.ITALIC
        }
    )

    @Suppress("Deprecation")
    val MODULE_USAGE_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
        "GLSL_MODULE_REF_HIGHLIGHT",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(255, 139, 70), Color(255, 139, 70))
            fontType = Font.ITALIC
        }
    )

    @Suppress("Deprecation")
    val MODULE_IMPORT_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
        "KIG_IMPORT_HIGHLIGHT",
        TextAttributes().apply {
            foregroundColor = JBColor(Color(255, 139, 70), Color(255, 139, 70))
        }
    )

    val IMPORTED_MODULE_ATTRIBUTE = TextAttributes().apply {
        foregroundColor = JBColor(Color(80, 160, 255), Color(80, 160, 255))
        effectType = EffectType.LINE_UNDERSCORE
        effectColor = JBColor(Color(80, 160, 255), Color(80, 160, 255))
    }

    val MODULE_IMPORT_SPECIAL_RENDER_BADGE_COLOR =
        JBColor(Color(255, 136, 64, 200), Color(255, 136, 64, 200))

    val MODULE_IMPORT_SPECIAL_RENDER_BADGE_TEXT_COLOR =
        JBColor(Color.WHITE, Color.WHITE)

    val MODULE_IMPORT_SPECIAL_RENDER_MODULE_TEXT_BG_COLOR =
        JBColor(Color(80, 160, 255, 60), Color(80, 160, 255, 60))
}
