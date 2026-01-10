package com.tttsaurus.ksml.language.embed.inject

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiLanguageInjectionHost
import com.tttsaurus.ksml.language.GlslLanguage
import com.tttsaurus.ksml.language.embed.KiGLanguage

class KsmlInGlslInjector : LanguageInjector {

    private val LOGGER : Logger = Logger.getInstance(KsmlInGlslInjector::class.java)

    init {
        LOGGER.info("KsmlInGlslInjector Created")
    }

    override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
        val comment = host as? PsiComment ?: return
        val file = comment.containingFile ?: return
        if (!file.isPhysical) return
        if (file.language != GlslLanguage.GLSL_LANGUAGE) return

        val raw = comment.text
        val index = raw.indexOf("@import")
        if (index < 0) return

        val afterSlashes = raw.removePrefix("//").trimStart()
        if (!afterSlashes.startsWith("@import")) return

        val end = raw.indexOfLast { !it.isWhitespace() } + 1
        if (end <= index) return

        places.addPlace(
            KiGLanguage.Companion.INSTANCE,
            TextRange(index, end),
            null,
            null
        )
    }
}
