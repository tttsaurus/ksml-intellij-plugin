package com.tttsaurus.ksml.language.navigation.reference.resolver

import com.intellij.injected.editor.DocumentWindow
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.util.IncorrectOperationException
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.utils.glsl.GlslPsiElementFactory

class KiGFuncCallReferenceResolver(
    private val index: Int,
    private val moduleName: String,
    private val moduleCallArgs: List<String>,
    element: PsiElement,
    range: TextRange,
    soft: Boolean
) : PsiReferenceBase<PsiElement>(
    element,
    range,
    soft
) {

    override fun resolve(): PsiElement? {
        val project = element.project
        if (project.isDisposed) return null

        if (DumbService.isDumb(project)) return null

        val functionName = rangeInElement.substring(element.text)

        val decls = SymbolIndexEntrypoint.getMatchingCodeDecls(project, functionName)

        val decl = decls.elementAtOrNull(index) ?: return null
        if (decl.moduleName == moduleName) {
            if (decl.params?.size == moduleCallArgs.size) {
                return decl
            }
        }
        return null
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val project = element.project

        if (!InjectedLanguageManager
                .getInstance(project)
                .isInjectedFragment(element.containingFile)
        ) {
            return element.replace(
                GlslPsiElementFactory.makeModuleFunctionCallFuncNameIdentifier(
                    project,
                    newElementName
                )
            )
        }

        val injectedFile = element.containingFile

        val documentWindow = PsiDocumentManager
            .getInstance(project)
            .getDocument(injectedFile) as? DocumentWindow
            ?: throw IncorrectOperationException(
                "Injected GLSL document is not editable."
            )

        documentWindow.replaceString(
            element.textRange.startOffset,
            element.textRange.endOffset,
            newElementName
        )

        PsiDocumentManager.getInstance(project).commitDocument(documentWindow.delegate)

        return element
    }
}
