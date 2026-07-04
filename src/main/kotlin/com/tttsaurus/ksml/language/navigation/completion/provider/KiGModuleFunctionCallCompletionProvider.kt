package com.tttsaurus.ksml.language.navigation.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.utils.ModuleFunctionCallGlVersionChecker
import com.tttsaurus.ksml.language.utils.StringExtensions.fuzzyMatchScore
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer
import kotlin.collections.withIndex

class KiGModuleFunctionCallCompletionProvider : CompletionProvider<CompletionParameters>() {

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

        if (file.fileType.defaultExtension != "glsl") return

        if (startOffset < 1) return
        if (chars[startOffset - 1] != '.') return
        if (startOffset < 2) return

        val moduleName = findModuleName(chars.substring(0, params.offset), startOffset - 2) ?: return
        if (moduleName.first().isDigit()) return

        val files = SymbolIndexEntrypoint.getMatchingFiles(project, moduleName)

        if (files.isEmpty()) return

        val searchList = mutableListOf<Pair<String, Int>>()

        for (file in files) {
            val psiFile = PsiManager.getInstance(project).findFile(file) ?: continue
            val ksmlFile = psiFile as? KsmlFile ?: continue

            if (moduleName != ksmlFile.moduleName) continue

            for (loc in ksmlFile.codeDeclLocations) {
                val functionName = loc.functionName ?: continue

                val score = input.fuzzyMatchScore(functionName)
                if (score >= 0) {
                    searchList += functionName to score
                }
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
            val codeDecls = SymbolIndexEntrypoint.getMatchingCodeDecls(project, search.first)
            for (codeDecl in codeDecls) {
                if (codeDecl.moduleName == moduleName) {
                    result.addElement(buildCompletionResult(project, file, codeDecl))
                }
            }
        }
    }

    private fun buildCompletionResult(project: Project, file: PsiFile, codeDecl: KsmlCodeDecl): LookupElement {
        val functionName = codeDecl.functionName ?: "UNKNOWN_FUNCTION"

        val moduleGlVersion = codeDecl.moduleGlVersion
        val functionGlVersion = codeDecl.funcGlVersion

        var glVersion: String? = null
        if (functionGlVersion != null) {
            glVersion = "GL$functionGlVersion${GlslProfileInferencer.getProfileDescSymbol(codeDecl.funcGlVersionIdent)}"
        } else if (moduleGlVersion != null) {
            glVersion = "GL$moduleGlVersion${GlslProfileInferencer.getProfileDescSymbol(codeDecl.moduleGlVersionIdent)}"
        }

        val tail = StringBuilder()
        tail.append("(")
        val params = codeDecl.params
        if (params != null) {
            for ((i, element) in params.withIndex()) {
                tail.append(element)
                if (i < params.size - 1) {
                    tail.append(", ")
                }
            }
        }
        tail.append(") ")
        if (glVersion != null) {
            tail.append("[").append(glVersion).append("]")
        }

        val bold: Boolean = codeDecl.isExport && ModuleFunctionCallGlVersionChecker
            .doesFileHaveRequiredGlVersion(
                project,
                file,
                codeDecl
            ).first

        val insertContent = StringBuilder()
        insertContent.append("(")
        if (params != null) {
            for (i in params.indices) {
                if (i < params.size - 1) {
                    insertContent.append(", ")
                }
            }
        }
        insertContent.append(")")

        val typeText = StringBuilder()
        typeText.append(codeDecl.returnType ?: "UNKNOWN_TYPE")
        typeText.append(" | <${codeDecl.moduleFileName ?: "UNKNOWN_FILE"}>")

        return PrioritizedLookupElement.withPriority(
            LookupElementBuilder
                .createWithSmartPointer(functionName, codeDecl)
                .withPresentableText(functionName)
                .withTypeText(typeText.toString(), true)
                .withTailText(tail.toString(), true)
                .withBoldness(bold)
                .withIcon(AllIcons.Nodes.Function)
                .withInsertHandler { context, element ->
                    val editor = context.editor
                    val document = editor.document
                    val offset = context.tailOffset

                    document.insertString(offset, insertContent.toString())
                    context.commitDocument()
                    editor.caretModel.moveToOffset(offset + 1)
                },
            (if (bold) 10 else 0).toDouble()
        )
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
