package com.tttsaurus.ksml.embed

import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiLanguageInjectionHost
import com.tttsaurus.ksml.GlslLanguage

class KsmlInGlslInjector : LanguageInjector {

    override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
        val comment = host as? PsiComment ?: return

        if (comment.containingFile.language != GlslLanguage.GLSL_LANGUAGE) return

        val raw = comment.text
        val index = raw.indexOf("@import")
        if (index < 0) return

        val afterSlashes = raw.removePrefix("//").trimStart()
        if (!afterSlashes.startsWith("@import")) return

        val end = raw.indexOfLast { !it.isWhitespace() } + 1
        if (end <= index) return

        places.addPlace(
            KiGLanguage,
            TextRange(index, end),
            null,
            null
        )
    }
}
