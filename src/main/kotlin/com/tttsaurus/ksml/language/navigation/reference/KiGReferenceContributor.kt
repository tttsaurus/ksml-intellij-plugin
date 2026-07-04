package com.tttsaurus.ksml.language.navigation.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.tttsaurus.ksml.language.GlslLanguage
import com.tttsaurus.ksml.language.navigation.reference.provider.KiGFuncCallReferenceProvider
import com.tttsaurus.ksml.language.navigation.reference.provider.KiGModuleImportReferenceProvider
import com.tttsaurus.ksml.language.navigation.reference.provider.KiGModuleCallReferenceProvider

class KiGReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns
                .psiElement(PsiComment::class.java)
                .withLanguage(GlslLanguage.GLSL_LANGUAGE),
            KiGModuleImportReferenceProvider()
        )
        registrar.registerReferenceProvider(
            PlatformPatterns
                .psiElement(PsiElement::class.java)
                .withLanguage(GlslLanguage.GLSL_LANGUAGE),
            KiGFuncCallReferenceProvider()
        )
        registrar.registerReferenceProvider(
            PlatformPatterns
                .psiElement(PsiElement::class.java)
                .withLanguage(GlslLanguage.GLSL_LANGUAGE),
            KiGModuleCallReferenceProvider()
        )
    }
}
