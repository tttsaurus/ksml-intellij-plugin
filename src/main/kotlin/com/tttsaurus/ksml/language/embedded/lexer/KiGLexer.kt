package com.tttsaurus.ksml.language.embedded.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.tttsaurus.ksml.language.embedded.KiGTypes

class KiGLexer : LexerBase() {

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
            return
        }

        // comment
        if (matchesAt(start, "//")) {
            end = start + 2
            tokenType = KiGTypes.COMMENT
            return
        }

        // import
        if (matchesAt(start, "@import")) {
            end = start + 7
            tokenType = KiGTypes.IMPORT
            return
        }

        // identifier
        while (end < bufferEnd && !buffer[end].isWhitespace()) end++
        tokenType = KiGTypes.IDENT
    }

    private fun matchesAt(pos: Int, s: String): Boolean {
        if (pos + s.length > bufferEnd) return false
        for (i in s.indices) {
            if (buffer[pos + i] != s[i]) return false
        }
        return true
    }
}
