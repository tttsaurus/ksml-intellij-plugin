package com.tttsaurus.ksml.language.reference

import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.tttsaurus.ksml.language.GlslLanguage
import com.tttsaurus.ksml.language.reference.provider.KiGImportReferenceProvider

class KiGReferenceContributor : PsiReferenceContributor() {

    private val LOGGER : Logger = Logger.getInstance(KiGReferenceContributor::class.java)

    init {
        LOGGER.info("KiGReferenceContributor Created")
    }

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns
                .psiElement(PsiComment::class.java)
                .withLanguage(GlslLanguage.GLSL_LANGUAGE),
            KiGImportReferenceProvider()
        )
    }
}
