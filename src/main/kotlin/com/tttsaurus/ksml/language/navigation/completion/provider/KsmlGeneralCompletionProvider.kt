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
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionRequirement
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
            val lineStart = chars.subSequence(0, params.offset).lastIndexOf('\n') + 1
            val prefix = chars.subSequence(lineStart, params.offset).toString()
            val version = Regex("""^[\t ]*@(gl_version|gl_requires)[\t ]+([0-9]*)$""").matchEntire(prefix)
            if (version != null) {
                GlslVersionRequirement.supportedVersions.forEach {
                    result.addElement(LookupElementBuilder.create(it.toString()).withIcon(AllIcons.Nodes.Aspect))
                }
                return
            }

            val profile = Regex("""^[\t ]*@(gl_version|gl_requires)[\t ]+(\d+)[\t ]+[A-Za-z_]*$""").matchEntire(prefix)
            if (profile != null) {
                val number = profile.groupValues[2].toIntOrNull() ?: return
                if (number in GlslVersionRequirement.supportedVersions && number >= 150) {
                    result.addAllElements(listOf("core", "compat", "compatibility").map { LookupElementBuilder.create(it) })
                }
                return
            }

            if (!Regex("""^[\t ]*@requires[\t ]+[A-Za-z_0-9]*$""").matches(prefix)) return
            val original = params.originalFile as? KsmlFile ?: return
            val imported = original.requiredModules.filter { it.range.startOffset !in lineStart..params.offset }.map { it.moduleName }.toSet()
            val modules = SymbolIndexEntrypoint.getAllModules(project)
                .asSequence()
                .filter { it != original.moduleName && it !in imported }
                .map { it to input.fuzzyMatchScore(it) }.filter { it.second >= 0 }
                .distinctBy { it.first }.sortedWith(compareByDescending<Pair<String, Int>> { it.second }.thenBy { it.first })
                .toList()

            for ((name, _) in modules) {
                result.addElement(PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(name).withBoldness(true).withIcon(KsmlIcons.FILE), 1000.0
                ))
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
