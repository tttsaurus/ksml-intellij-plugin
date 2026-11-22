package com.tttsaurus.ksml.language.utils.glsl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiComment
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

object GlslFileModuleImports {

    data class ImportedModule(
        val moduleName: String,
        val range: TextRange,
        val directiveOffset: Int
    )

    private val IMPORT_REGEX = Regex("""^//[\t ]*@import[\t ]+([a-zA-Z_][a-zA-Z0-9_]*)\b""")

    fun getImportedModules(file: PsiFile): List<ImportedModule> {
        return CachedValuesManager.getCachedValue(file) {
            val imports = PsiTreeUtil.findChildrenOfType(file, PsiComment::class.java).mapNotNull { comment ->
                val match = IMPORT_REGEX.find(comment.text) ?: return@mapNotNull null
                val group = match.groups[1]!!
                val offset = comment.textRange.startOffset
                ImportedModule(
                    group.value,
                    TextRange(offset + group.range.first, offset + group.range.last + 1),
                    offset + comment.text.indexOf("@import")
                )
            }

            CachedValueProvider.Result.create(
                imports,
                file
            )
        }
    }
}
