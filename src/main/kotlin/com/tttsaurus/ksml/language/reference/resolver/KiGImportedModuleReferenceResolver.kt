package com.tttsaurus.ksml.language.reference.resolver

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.language.embed_lang.psi.KiGIdentDecl
import com.tttsaurus.ksml.language.utils.GlslFileModuleImports

class KiGImportedModuleReferenceResolver(
    private val moduleName: String,
    element: PsiElement,
    rangeInElement: TextRange,
    soft: Boolean
) : PsiReferenceBase<PsiElement>(
    element,
    rangeInElement,
    soft
) {

    override fun resolve(): PsiElement? {
        val file = element.containingFile

        var found = false
        var textRange: TextRange? = null
        GlslFileModuleImports.getImportedModules(file).forEach {
            if (it.moduleName == moduleName) {
                found = true
                textRange = it.range
            }
        }
        if (!found) return null

        val comment = file.findElementAt(textRange!!.startOffset) ?: return null

        val injected = InjectedLanguageManager
            .getInstance(element.project)
            .getInjectedPsiFiles(comment)
            ?.firstOrNull()
            ?: return null

        val injectedFile = injected.first.containingFile
        val ident = PsiTreeUtil.getChildOfType(injectedFile, KiGIdentDecl::class.java) ?: return null

        return ident
    }
}
