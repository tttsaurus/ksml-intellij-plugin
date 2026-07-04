package com.tttsaurus.ksml.language.navigation.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.KsmlIcons
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.utils.StringExtensions.fuzzyMatchScore

class KsmlGeneralCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = params.position
        val project = position.project
        val file = position.containingFile
        val chars = params.editor.document.charsSequence
        val startOffset = position.node.startOffset

        if (params.offset > chars.length) return
        val input = chars.substring(startOffset, params.offset)

        if (file.fileType.defaultExtension != "ksml") return

        if (startOffset >= 1 && chars[startOffset - 1] == '@') {
            result.addAllElements(
                listOf(
                    ksmlElement("module", space = true, newLine = false),
                    ksmlElement("requires", space = true, newLine = false),
                    ksmlElement("export", space = false, newLine = true),
                    ksmlElement("gl_version", space = true, newLine = false),
                    ksmlElement("gl_requires", space = true, newLine = false),
                    ksmlElement("feature", space = true, newLine = false),
                    ksmlElement("code", space = false, newLine = false) // code block has its own insert behavior
                )
            )
        } else {
            val searchList = mutableListOf<Pair<String, Int>>()
            val modules = SymbolIndexEntrypoint.getAllModules(project)

            for (module in modules) {
                val score = input.fuzzyMatchScore(module)
                if (score >= 0) {
                    searchList += module to score
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

    private fun ksmlElement(name: String, space: Boolean, newLine: Boolean) : LookupElement {
        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder
                .create(name)
                .withIcon(AllIcons.Nodes.Aspect)
                .withInsertHandler { context, element ->
                    val editor = context.editor
                    val document = editor.document
                    val offset = context.tailOffset

                    if (name == "code") {
                        document.insertString(offset, " ${"\"\"\""}\n\n${"\"\"\""}")
                        context.commitDocument()
                        editor.caretModel.moveToOffset(offset + 5)
                        return@withInsertHandler
                    }

                    if (!space && !newLine) return@withInsertHandler

                    if (space && newLine) {
                        document.insertString(offset, " \n")
                        context.commitDocument()
                        editor.caretModel.moveToOffset(offset + 2)
                    } else if (space) {
                        document.insertString(offset, " ")
                        context.commitDocument()
                        editor.caretModel.moveToOffset(offset + 1)
                    } else {
                        document.insertString(offset, "\n")
                        context.commitDocument()
                        editor.caretModel.moveToOffset(offset + 1)
                    }
                },
            2000.0
        )
    }
}
