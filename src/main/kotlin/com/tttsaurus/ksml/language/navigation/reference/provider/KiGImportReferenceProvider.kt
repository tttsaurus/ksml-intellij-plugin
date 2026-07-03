package com.tttsaurus.ksml.language.navigation.reference.provider

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.navigation.reference.resolver.KiGImportReferenceResolver

class KiGImportReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<out PsiReference> {

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
            val range = TextRange(startInComment, endInComment)

            val count = countOccurrences(range.substring(comment.text).trim(), comment.project)
            if (count <= 0) {
                refs += KiGImportReferenceResolver(
                    0,
                    comment,
                    range,
                    true
                )
            } else {
                for (i in 0 until count) {
                    refs += KiGImportReferenceResolver(
                        i,
                        comment,
                        range,
                        true
                    )
                }
            }
        }

        return refs.toTypedArray()
    }

    private fun countOccurrences(name: String, project: Project): Int {
        if (project.isDisposed) return 0
        if (DumbService.isDumb(project)) return 0

        if (name.isEmpty()) return 0

        val files = SymbolIndexEntrypoint.getMatchingFiles(project, name)

        return files.size
    }
}
