package com.tttsaurus.ksml.language.navigation.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.KsmlIcons
import com.tttsaurus.ksml.language.utils.StringExtensions.fuzzyMatchScore
import com.tttsaurus.ksml.language.utils.glsl.GlslFileModuleImports

class KiGModuleCallCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = params.position
        val file = position.containingFile
        val chars = params.editor.document.charsSequence
        val startOffset = position.node.startOffset

        if (params.offset > chars.length) return
        val input = chars.substring(startOffset, params.offset)

        if (file.fileType.defaultExtension != "glsl") return

        if (startOffset >= 1 && chars[startOffset - 1] == '.') return

        val searchList = mutableListOf<Pair<String, Int>>()
        val modules = GlslFileModuleImports.getImportedModules(file)

        for (module in modules) {
            val score = input.fuzzyMatchScore(module.moduleName)
            if (score >= 0) {
                searchList += module.moduleName to score
            }
        }

        if (searchList.isEmpty()) return

        searchList
            .distinctBy { it.first }
            .sortedWith(
                compareByDescending<Pair<String, Int>> { it.second }
                    .thenBy { it.first.length }
                    .thenBy { it.first }
            )

        for (search in searchList) {
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(search.first)
                        .withBoldness(true)
                        .withIcon(KsmlIcons.FILE),
                    1000.0
                )
            )
        }
    }
}
