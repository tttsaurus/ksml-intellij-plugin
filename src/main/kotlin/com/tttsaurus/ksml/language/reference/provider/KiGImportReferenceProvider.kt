package com.tttsaurus.ksml.language.reference.provider

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.reference.resolver.KiGImportReferenceResolver

class KiGImportReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<out PsiReference?> {

        val comment = element as? PsiComment
            ?: return PsiReference.EMPTY_ARRAY

        val text = comment.text

        val importPrefix = "@import"
        val prefixIndex = text.indexOf(importPrefix)
        if (prefixIndex == -1) return PsiReference.EMPTY_ARRAY

        val refs = ArrayList<PsiReference>()

        val contentStartIndex = prefixIndex + importPrefix.length
        val content = text.substring(contentStartIndex)

        val regex = Regex("""[a-zA-Z_][a-zA-Z0-9_]*""")
        val matches = regex.findAll(content)

        for (match in matches) {
            val startInComment = contentStartIndex + match.range.first
            val endInComment = contentStartIndex + match.range.last + 1

            // the text range is accurate here
            // but intellij doesn't display the reference range correctly
            refs += KiGImportReferenceResolver(
                comment,
                TextRange(startInComment, endInComment),
                true
            )
        }

        return refs.toTypedArray()
    }
}
