package com.tttsaurus.ksml.language.reference.provider

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.embed_lang.KiGLanguage
import com.tttsaurus.ksml.language.embed_lang.KiGTypes
import com.tttsaurus.ksml.language.reference.resolver.KiGImportReferenceResolver

class KiGImportReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<out PsiReference?> {

        val comment = element as? PsiComment
            ?: return PsiReference.EMPTY_ARRAY

        val ilm = InjectedLanguageManager.getInstance(comment.project)
        val injected = ilm.getInjectedPsiFiles(comment)
            ?: return PsiReference.EMPTY_ARRAY

        val refs = ArrayList<PsiReference>()

        for (pair in injected) {
            val injectedRoot = pair.first as PsiElement
            val hostRangeOfFragment = pair.second as TextRange

            if (injectedRoot.language != KiGLanguage.INSTANCE) continue

            val all = ArrayList<PsiElement>()
            collectAllElements(injectedRoot, all)

            val idents = all.filter {
                PsiUtilCore.getElementType(it) == KiGTypes.IDENT
            }

            for (ident in idents) {
                val injectedStart = ident.textRange.startOffset
                val injectedEnd = ident.textRange.endOffset

                val hostStartAbs = hostRangeOfFragment.startOffset + injectedStart
                val hostEndAbs = hostRangeOfFragment.startOffset + injectedEnd

                val startInComment = hostStartAbs - comment.textRange.startOffset
                val endInComment = hostEndAbs - comment.textRange.startOffset

                val rangeInComment = clampRangeToElement(
                    comment,
                    startInComment,
                    endInComment
                ) ?: continue

                refs += KiGImportReferenceResolver(
                    comment,
                    rangeInComment,
                    true
                )

            }
        }

        return refs.toTypedArray()
    }

    private fun collectAllElements(root: PsiElement, out: MutableList<PsiElement>) {
        out += root
        for (child in root.children) {
            collectAllElements(child, out)
        }
    }

    private fun clampRangeToElement(
        element: PsiElement,
        start: Int,
        end: Int
    ): TextRange? {
        val length = element.textLength
        if (length <= 0) return null

        val s = start.coerceIn(0, length)
        val e = end.coerceIn(0, length)

        if (s >= e) return null
        return TextRange(s, e)
    }
}
