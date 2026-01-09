package com.tttsaurus.ksml.embed

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.GlslLanguage
import com.tttsaurus.ksml.KsmlLanguage
import com.tttsaurus.ksml.grammar.psi.AnyChunk
import com.tttsaurus.ksml.grammar.psi.KsmlAnnotation

class GlslInKsmlInjector : LanguageInjector {

    private val LOGGER : Logger = Logger.getInstance(GlslInKsmlInjector::class.java)

    init {
        LOGGER.info("GlslInKsmlInjector Created")
    }

    override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
        val chunk = host as? AnyChunk ?: return
        val file = chunk.containingFile ?: return
        if (!file.isPhysical) return

        LOGGER.info("GlslInKsmlInjector Debug: " + file.language.toString())

        if (file.language != KsmlLanguage) return

        if (PsiTreeUtil.getParentOfType(chunk, KsmlAnnotation::class.java) != null) return

        places.addPlace(
            GlslLanguage.GLSL_LANGUAGE,
            TextRange.from(0, chunk.textLength),
            null,
            null
        )
    }
}
