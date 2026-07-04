package com.tttsaurus.ksml.language.navigation.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.tttsaurus.ksml.language.GlslLanguage
import com.tttsaurus.ksml.language.navigation.completion.provider.GlslModuleCallCompletionProvider
import com.tttsaurus.ksml.language.navigation.completion.provider.GlslModuleFunctionCallCompletionProvider

class GlslCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns
                .psiElement(PsiElement::class.java)
                .withLanguage(GlslLanguage.GLSL_LANGUAGE),
            GlslModuleFunctionCallCompletionProvider()
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns
                .psiElement(PsiElement::class.java)
                .withLanguage(GlslLanguage.GLSL_LANGUAGE),
            GlslModuleCallCompletionProvider()
        )
    }
}
