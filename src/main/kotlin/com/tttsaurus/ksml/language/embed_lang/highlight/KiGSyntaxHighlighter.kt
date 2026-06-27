package com.tttsaurus.ksml.language.embed_lang.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType
import com.tttsaurus.ksml.language.embed_lang.lexer.KiGLexer
import com.tttsaurus.ksml.language.embed_lang.KiGTypes

class KiGSyntaxHighlighter : SyntaxHighlighter {

    override fun getHighlightingLexer(): Lexer = KiGLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        when (tokenType) {
            KiGTypes.COMMENT -> arrayOf(KiGTextAttributes.COMMENT)
            KiGTypes.IMPORT -> arrayOf(KiGTextAttributes.IMPORT)
            KiGTypes.IDENT -> arrayOf(KiGTextAttributes.IDENT)
            else -> TextAttributesKey.EMPTY_ARRAY
        }
}
