package com.tttsaurus.ksml.language.inject

import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.GlslLanguage
import com.tttsaurus.ksml.language.KsmlLanguage

class GlslInKsmlInjector : LanguageInjector {

    override fun getLanguagesToInject(host: PsiLanguageInjectionHost, places: InjectedLanguagePlaces) {
        val codeDecl = host as? KsmlCodeDecl ?: return
        val file = codeDecl.containingFile ?: return
        if (!file.isPhysical) return
        if (file.language != KsmlLanguage.INSTANCE) return

        val codeBlock = codeDecl.codeBlock
        val codeText = codeBlock.text
        if (codeText.length < 6) return
        if (!codeText.startsWith("\"\"\"") || !codeText.endsWith("\"\"\"")) return

        val startOffset = codeBlock.startOffsetInParent + 3
        val endOffset = codeBlock.startOffsetInParent + codeText.length - 3
        if (endOffset <= startOffset) return

        places.addPlace(
            GlslLanguage.GLSL_LANGUAGE,
            TextRange(startOffset, endOffset),
            null,
            null
        )
    }
}
