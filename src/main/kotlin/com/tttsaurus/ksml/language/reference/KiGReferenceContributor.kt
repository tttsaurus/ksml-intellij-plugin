package com.tttsaurus.ksml.language.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.tttsaurus.ksml.language.GlslLanguage
import com.tttsaurus.ksml.language.reference.provider.KiGFunctionReferenceProvider
import com.tttsaurus.ksml.language.reference.provider.KiGImportReferenceProvider

class KiGReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns
                .psiElement(PsiComment::class.java)
                .withLanguage(GlslLanguage.GLSL_LANGUAGE),
            KiGImportReferenceProvider()
        )
        registrar.registerReferenceProvider(
            PlatformPatterns
                .psiElement(PsiElement::class.java)
                .withLanguage(GlslLanguage.GLSL_LANGUAGE),
            KiGFunctionReferenceProvider()
        )
    }
}
