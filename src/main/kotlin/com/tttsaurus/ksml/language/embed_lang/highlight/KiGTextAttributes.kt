package com.tttsaurus.ksml.language.embed_lang.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object KiGTextAttributes {
    val IMPORT =
        TextAttributesKey.createTextAttributesKey(
            "KIG_IMPORT",
            DefaultLanguageHighlighterColors.KEYWORD
        )

    val IDENT =
        TextAttributesKey.createTextAttributesKey(
            "KIG_IDENT",
            DefaultLanguageHighlighterColors.IDENTIFIER
        )
}
