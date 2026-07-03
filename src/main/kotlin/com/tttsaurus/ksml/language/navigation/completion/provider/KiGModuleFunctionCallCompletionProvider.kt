package com.tttsaurus.ksml.language.navigation.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint

class KiGModuleFunctionCallCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = params.position
        val project = position.project
        val chars = params.editor.document.charsSequence
        val startOffset = position.node.startOffset
        val input = chars.substring(startOffset, params.offset)

        if (startOffset < 1) return
        if (chars[startOffset - 1] != '.') return
        if (startOffset < 2) return

        val moduleName = findModuleName(chars.substring(0, params.offset), startOffset - 2) ?: return
        if (moduleName.first().isDigit()) return

        val files = SymbolIndexEntrypoint.getMatchingFiles(project, moduleName)

        println("debug file size: ${files.size}")

        if (files.isEmpty()) return
    }

    /**
     * Offset must be the index right before the period `.`.
     */
    private fun findModuleName(text: CharSequence, offset: Int): String? {
        if (offset < 0 || offset >= text.length) {
            return null
        }

        var start = offset
        while (start >= 0 && isModuleChar(text[start])) {
            start--
        }

        start++

        if (start > offset) {
            return null
        }

        return text.subSequence(start, offset + 1).toString()
    }

    private fun isModuleChar(c: Char): Boolean {
        return c == '_' || c.isLetterOrDigit()
    }
}
