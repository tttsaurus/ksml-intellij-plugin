package com.tttsaurus.ksml.embed

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.tttsaurus.ksml.KsmlLanguage
import org.jetbrains.annotations.Unmodifiable

class GlslInKsmlInjector : MultiHostInjector {

    private val LOGGER : Logger = Logger.getInstance(GlslInKsmlInjector::class.java)

    init {
        LOGGER.info("GlslInKsmlInjector Created")
    }

    override fun getLanguagesToInject(
        registrar: MultiHostRegistrar,
        host: PsiElement
    ) {
        val file = host.containingFile ?: return

        LOGGER.info("GlslInKsmlInjector Debug: " + host.text)

        if (file.language != KsmlLanguage) return
    }

    override fun elementsToInjectIn(): @Unmodifiable List<Class<out PsiElement>> {
        return listOf(PsiElement::class.java)
    }
}
