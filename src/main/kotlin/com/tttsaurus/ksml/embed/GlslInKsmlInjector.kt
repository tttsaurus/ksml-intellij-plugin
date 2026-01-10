package com.tttsaurus.ksml.embed

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiLanguageInjectionHost
import com.tttsaurus.ksml.GlslLanguage
import com.tttsaurus.ksml.KsmlLanguage

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

        places.addPlace(
            GlslLanguage.GLSL_LANGUAGE,
            TextRange(0, comment.textLength),
            null,
            null
        )
    }
}
