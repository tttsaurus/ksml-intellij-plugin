package com.tttsaurus.ksml.language.embed.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType
import com.tttsaurus.ksml.language.embed.lexer.KiGLexer
import com.tttsaurus.ksml.language.embed.lexer.KiGTokenTypes

class KiGSyntaxHighlighter : SyntaxHighlighter {

    private val LOGGER : Logger = Logger.getInstance(KiGSyntaxHighlighter::class.java)

    init {
        LOGGER.info("KiGSyntaxHighlighter Created")
    }

    override fun getHighlightingLexer(): Lexer = KiGLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        when (tokenType) {
            KiGTokenTypes.IMPORT -> arrayOf(KiGTextAttributes.IMPORT)
            KiGTokenTypes.IDENT -> arrayOf(KiGTextAttributes.IDENT)
            else -> TextAttributesKey.EMPTY_ARRAY
        }
}
