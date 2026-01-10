package com.tttsaurus.ksml.language.embed.inject

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiLanguageInjectionHost
import com.tttsaurus.ksml.language.GlslLanguage
import com.tttsaurus.ksml.language.KsmlLanguage

class GlslInKsmlInjector : LanguageInjector {

    private val LOGGER : Logger = Logger.getInstance(GlslInKsmlInjector::class.java)

    init {
        LOGGER.info("GlslInKsmlInjector Created")
    }

    override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
        val comment = host as? PsiComment ?: return
        val file = comment.containingFile ?: return
        if (!file.isPhysical) return
        if (file.language != KsmlLanguage.INSTANCE) return

        val text = comment.text
        if (!text.startsWith("/*") || !text.endsWith("*/")) return

        places.addPlace(
            GlslLanguage.GLSL_LANGUAGE,
            TextRange(2, text.length - 2),
            null,
            null
        )
    }
}
