package com.tttsaurus.ksml.language.navigation.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.tttsaurus.ksml.language.KsmlLanguage
import com.tttsaurus.ksml.language.navigation.completion.provider.KsmlGeneralCompletionProvider

class KsmlCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns
                .psiElement(PsiElement::class.java)
                .withLanguage(KsmlLanguage.INSTANCE),
            KsmlGeneralCompletionProvider()
        )
    }
}
