package com.tttsaurus.ksml.language.navigation.reference.resolver

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.grammar.psi.KsmlModuleDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.embedded.psi.KiGIdentDecl
import com.tttsaurus.ksml.language.utils.glsl.GlslFileModuleImports

class KiGModuleCallReferenceResolver(
    private val index: Int,
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
        val file = element.containingFile ?: return null

        val langInjectionManager = InjectedLanguageManager.getInstance(element.project)
        if (langInjectionManager.isInjectedFragment(file)) {
            val host = langInjectionManager.getInjectionHost(file) ?: return null
            val hostFile = host.containingFile ?: return null
            if (hostFile !is KsmlFile) return null

            val project = element.project
            if (project.isDisposed) return null
            if (DumbService.isDumb(project)) return null
            if (moduleName.isEmpty()) return null

            val files = SymbolIndexEntrypoint.getMatchingFiles(project, moduleName)

            val vFile = files.elementAtOrNull(index) ?: return null
            val psiFile = PsiManager.getInstance(project).findFile(vFile) ?: return null

            val decls = PsiTreeUtil.findChildrenOfType(psiFile, KsmlModuleDecl::class.java)
            val targetDecl = decls.firstOrNull { it.text.contains(moduleName) } ?: return psiFile

            return targetDecl
        }

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

        val injectedFile = injected.first.containingFile ?: return null
        val ident = PsiTreeUtil.getChildOfType(injectedFile, KiGIdentDecl::class.java) ?: return null

        return ident
    }
}
