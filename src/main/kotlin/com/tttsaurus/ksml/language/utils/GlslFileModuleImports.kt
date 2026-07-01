package com.tttsaurus.ksml.language.utils

import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

object GlslFileModuleImports {

    private val IMPORT_REGEX = Regex("""//\s*@import\s+([a-zA-Z_][a-zA-Z0-9_]*)""")

    fun getImportedModules(file: PsiFile): Set<String> {
        return CachedValuesManager.getCachedValue(file) {
            val text = file.text
            val imports = IMPORT_REGEX
                .findAll(text)
                .map { it.groupValues[1] }
                .toSet()

            CachedValueProvider.Result.create(
                imports,
                file
            )
        }
    }
}
