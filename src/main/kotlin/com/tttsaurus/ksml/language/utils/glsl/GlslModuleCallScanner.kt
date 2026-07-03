package com.tttsaurus.ksml.language.utils.glsl

class GlslModuleCallScanner(private val text: CharSequence) {

    data class ModuleCall(
        val module: String,
        val function: String,
        val moduleStart: Int,
        val moduleEnd: Int,
        val functionStart: Int,
        val functionEnd: Int
    )

    private enum class State {
        OUTER,
        NORMAL,
        STRING,
        CHAR,
        LINE_COMMENT,
        BLOCK_COMMENT,
        PREPROCESSOR
    }

    companion object {
        private const val TRIPLE_QUOTE = "\"\"\""
    }

    private val hasTripleBlocks = text.indexOf(TRIPLE_QUOTE) >= 0
    private var state: State = if (hasTripleBlocks) State.OUTER else State.NORMAL

    private var index = 0
    private var escaped = false

    fun nextCall(): ModuleCall? {
        while (index < text.length) {
            when (state) {
                State.OUTER -> {
                    if (startsWithTripleQuote(index)) {
                        index += 3
                        state = State.NORMAL
                    } else {
                        index++
                    }
                }

                State.NORMAL -> {
                    if (hasTripleBlocks && startsWithTripleQuote(index)) {
                        index += 3
                        state = State.OUTER
                        continue
                    }

                    val call = scanNormal()
                    if (call != null) return call
                }

                State.STRING -> consumeString()
                State.CHAR -> consumeChar()
                State.LINE_COMMENT -> consumeLineComment()
                State.BLOCK_COMMENT -> consumeBlockComment()
                State.PREPROCESSOR -> consumePreprocessor()
            }
        }

        return null
    }

    private fun scanNormal(): ModuleCall? {
        val c = text[index]

        when {
            c == '"' -> {
                state = State.STRING
                escaped = false
                index++
                return null
            }

            c == '\'' -> {
                state = State.CHAR
                escaped = false
                index++
                return null
            }

            c == '/' && peek(1) == '/' -> {
                state = State.LINE_COMMENT
                index += 2
                return null
            }

            c == '/' && peek(1) == '*' -> {
                state = State.BLOCK_COMMENT
                index += 2
                return null
            }

            c == '#' && isLineStart(index) -> {
                state = State.PREPROCESSOR
                index++
                return null
            }

            !isIdentifierStart(c) -> {
                index++
                return null
            }
        }

        val moduleStart = index

        while (index < text.length && isIdentifierPart(text[index])) {
            index++
        }

        val moduleEnd = index

        if (index >= text.length || text[index] != '.') {
            return null
        }

        index++

        if (index >= text.length || !isIdentifierStart(text[index])) {
            return null
        }

        val functionStart = index

        while (index < text.length && isIdentifierPart(text[index])) {
            index++
        }

        val functionEnd = index

        var j = index
        while (j < text.length && text[j].isWhitespace()) {
            j++
        }

        if (j >= text.length || text[j] != '(') {
            return null
        }

        return ModuleCall(
            module = text.subSequence(moduleStart, moduleEnd).toString(),
            function = text.subSequence(functionStart, functionEnd).toString(),
            moduleStart = moduleStart,
            moduleEnd = moduleEnd,
            functionStart = functionStart,
            functionEnd = functionEnd
        )
    }

    private fun consumeString() {
        val c = text[index]

        if (escaped) {
            escaped = false
            index++
            return
        }

        when (c) {
            '\\' -> escaped = true
            '"' -> state = State.NORMAL
        }

        index++
    }

    private fun consumeChar() {
        val c = text[index]

        if (escaped) {
            escaped = false
            index++
            return
        }

        when (c) {
            '\\' -> escaped = true
            '\'' -> state = State.NORMAL
        }

        index++
    }

    private fun consumeLineComment() {
        if (text[index] == '\n') {
            state = State.NORMAL
        }

        index++
    }

    private fun consumeBlockComment() {
        if (text[index] == '*' && peek(1) == '/') {
            state = State.NORMAL
            index += 2
            return
        }

        index++
    }

    private fun consumePreprocessor() {
        if (text[index] == '\n') {
            state = State.NORMAL
        }

        index++
    }

    private fun isLineStart(offset: Int): Boolean {
        var i = offset - 1

        while (i >= 0 && (text[i] == ' ' || text[i] == '\t')) {
            i--
        }

        return i < 0 || text[i] == '\n'
    }

    private fun startsWithTripleQuote(offset: Int): Boolean {
        return offset + 2 < text.length &&
                text[offset] == '"' &&
                text[offset + 1] == '"' &&
                text[offset + 2] == '"'
    }

    private fun peek(delta: Int): Char? {
        val i = index + delta
        return if (i in text.indices) text[i] else null
    }

    private fun isIdentifierStart(c: Char): Boolean {
        return c == '_' || c.isLetter()
    }

    private fun isIdentifierPart(c: Char): Boolean {
        return c == '_' || c.isLetterOrDigit()
    }
}
