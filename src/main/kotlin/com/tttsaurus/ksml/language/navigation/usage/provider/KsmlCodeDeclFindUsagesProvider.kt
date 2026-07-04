package com.tttsaurus.ksml.language.navigation.usage.provider

import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class KsmlCodeDeclFindUsagesProvider : FindUsagesProvider {

    override fun canFindUsagesFor(element: PsiElement): Boolean {
        if (element.containingFile !is KsmlFile) return false
        return element is KsmlCodeDecl
    }

    override fun getHelpId(element: PsiElement): @NonNls String? {
        return null
    }

    override fun getType(element: PsiElement): @Nls String {
        return "function"
    }

    override fun getDescriptiveName(element: PsiElement): @Nls String {
        return (element as? KsmlCodeDecl)?.functionName ?: ""
    }

    override fun getNodeText(
        element: PsiElement,
        bool: Boolean
    ): @Nls String {
        return getDescriptiveName(element)
    }
}
