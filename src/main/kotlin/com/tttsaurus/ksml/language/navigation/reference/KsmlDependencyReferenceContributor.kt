package com.tttsaurus.ksml.language.navigation.reference

import com.intellij.openapi.project.DumbService
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.grammar.psi.KsmlRequiresDecl
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.navigation.reference.resolver.KiGModuleImportReferenceResolver

class KsmlDependencyReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(KsmlRequiresDecl::class.java), object : PsiReferenceProvider() {
            override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<out PsiReference> {
                val declaration = element as? KsmlRequiresDecl ?: return PsiReference.EMPTY_ARRAY
                if (DumbService.isDumb(element.project)) return PsiReference.EMPTY_ARRAY
                val count = SymbolIndexEntrypoint.getMatchingFiles(element.project, declaration.identifier.text).size.coerceAtLeast(1)
                val range = declaration.identifier.textRange.shiftLeft(declaration.textRange.startOffset)
                return Array(count) { KiGModuleImportReferenceResolver(it, declaration, range, true) }
            }
        })
    }
}
