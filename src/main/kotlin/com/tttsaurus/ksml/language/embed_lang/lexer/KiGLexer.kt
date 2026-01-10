package com.tttsaurus.ksml.language.embed_lang.lexer

import com.intellij.lexer.LexerBase
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.tttsaurus.ksml.language.embed_lang.KiGTypes

class KiGLexer : LexerBase() {

    private val LOGGER : Logger = Logger.getInstance(KiGLexer::class.java)

    private lateinit var buffer: CharSequence
    private var start = 0
    private var end = 0
    private var bufferEnd = 0
    private var tokenType: IElementType? = null

    override fun start(
        buffer: CharSequence,
        startOffset: Int,
        endOffset: Int,
        initialState: Int
    ) {
        this.buffer = buffer
        this.start = startOffset
        this.end = startOffset
        this.bufferEnd = endOffset
        advance()
    }

    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = start
    override fun getTokenEnd(): Int = end
    override fun getState(): Int = 0
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = bufferEnd

    override fun advance() {
        if (end >= bufferEnd) {
            tokenType = null
            return
        }

        start = end
        val c = buffer[start]

        // whitespace
        if (c.isWhitespace()) {
            while (end < bufferEnd && buffer[end].isWhitespace()) end++
            tokenType = TokenType.WHITE_SPACE
            LOGGER.info("KiGLexer.advance: WHITE_SPACE, start=$start, end=$end")
            return
        }

        // @import
        if (matchesAt(start, "@import")) {
            end = start + 7
            tokenType = KiGTypes.IMPORT
            LOGGER.info("KiGLexer.advance: IMPORT, start=$start, end=$end")
            return
        }

        // identifier
        while (end < bufferEnd && !buffer[end].isWhitespace()) end++
        tokenType = KiGTypes.IDENT
        LOGGER.info("KiGLexer.advance: IDENT, start=$start, end=$end")
    }

    private fun matchesAt(pos: Int, s: String): Boolean {
        if (pos + s.length > bufferEnd) return false
        for (i in s.indices) {
            if (buffer[pos + i] != s[i]) return false
        }
        return true
    }
}
