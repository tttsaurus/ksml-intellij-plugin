package com.tttsaurus.ksml.language.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.psi.tree.IElementType
import com.tttsaurus.ksml.grammar.psi.KsmlTypes
import com.tttsaurus.ksml.language.parser.KsmlLexerAdapter

class KsmlSyntaxHighlighter : SyntaxHighlighter {

    private val LOGGER : Logger = Logger.getInstance(KsmlSyntaxHighlighter::class.java)

    init {
        LOGGER.info("KsmlSyntaxHighlighter Created")
    }

    override fun getHighlightingLexer(): Lexer =
        KsmlLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        when (tokenType) {
            KsmlTypes.AT -> AT_KEYS

            KsmlTypes.MODULE,
            KsmlTypes.REQUIRES,
            KsmlTypes.EXPORT,
            KsmlTypes.FEATURE,
            KsmlTypes.CODE,
            KsmlTypes.GL_VERSION,
            KsmlTypes.GL_REQUIRES -> KEYWORD_KEYS

            KsmlTypes.NUMBER -> NUMBER_KEYS

            KsmlTypes.CODE_BLOCK -> CODE_BLOCK_KEYS

            KsmlTypes.COMMENT -> COMMENT_KEYS

            else -> EMPTY_KEYS
        }

    companion object {
        private val AT_KEYS = arrayOf(
            TextAttributesKey.createTextAttributesKey(
                "KSML_AT",
                DefaultLanguageHighlighterColors.METADATA
            )
        )

        private val KEYWORD_KEYS = arrayOf(
            TextAttributesKey.createTextAttributesKey(
                "KSML_KEYWORD",
                DefaultLanguageHighlighterColors.KEYWORD
            )
        )

        private val NUMBER_KEYS = arrayOf(
            TextAttributesKey.createTextAttributesKey(
                "KSML_NUMBER",
                DefaultLanguageHighlighterColors.NUMBER
            )
        )

        private val CODE_BLOCK_KEYS = arrayOf(
            TextAttributesKey.createTextAttributesKey(
                "KSML_CODE_BLOCK",
                DefaultLanguageHighlighterColors.STRING
            )
        )

        private val COMMENT_KEYS = arrayOf(
            TextAttributesKey.createTextAttributesKey(
                "KSML_COMMENT",
                DefaultLanguageHighlighterColors.BLOCK_COMMENT
            )
        )

        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }
}
