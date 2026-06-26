package com.tttsaurus.ksml.language.embed_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import java.awt.Color

private val MODULE_REF_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
    "GLSL_MODULE_REF_HIGHLIGHT",
    TextAttributes().apply {
        foregroundColor = Color(255, 139, 70)
        effectType = EffectType.LINE_UNDERSCORE
        effectColor = Color(166, 210, 255, 120)
    }
)

private val IMPORT_REGEX = Regex("""//\s*@import\s+([a-zA-Z_][a-zA-Z0-9_]*)""")

class GlslModuleInExprAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return
        if (node.elementType.toString() != "UNARY_EXPR") return

        val text = element.text
        if (!text.contains(".")) return

        if (text.allIndicesOf(".").all { index ->
            (index >= 1 && index <= text.length - 2) &&
                    (text[index - 1].isDigit() && text[index + 1].isDigit())
        }) { return }

        val qualifier = text.substringBefore('.', missingDelimiterValue = "")
        if (qualifier.isEmpty()) return
        if (!qualifier.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))) return

        val file = element.containingFile ?: return
        val importedModules = file.getImportedModulesCached()

        if (qualifier !in importedModules) return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(
                element.textRange.startOffset,
                element.textRange.startOffset + qualifier.length
            ))
            .textAttributes(MODULE_REF_HIGHLIGHT)
            .create()
    }

    private fun String.allIndicesOf(sub: String): List<Int> {
        val result = mutableListOf<Int>()
        var index = indexOf(sub)

        while (index >= 0) {
            result.add(index)
            index = indexOf(sub, index + 1)
        }

        return result
    }

    private fun PsiFile.getImportedModulesCached(): Set<String> {
        val project = project

        return CachedValuesManager.getManager(project)
            .getCachedValue(this) {

                val text = text
                val imports = IMPORT_REGEX
                    .findAll(text)
                    .map { it.groupValues[1] }
                    .toSet()

                CachedValueProvider.Result.create(
                    imports,
                    PsiModificationTracker.MODIFICATION_COUNT
                )
            }
    }
}
