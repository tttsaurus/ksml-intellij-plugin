package com.tttsaurus.ksml.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class KsmlTestAnnotator : Annotator {

    private val LOGGER : Logger = Logger.getInstance(KsmlTestAnnotator::class.java)

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        LOGGER.info("KsmlTestAnnotator Debug: " + element.text)

        if (element is PsiFile) {
            LOGGER.warn(
                "children = " + element.children.joinToString { it.javaClass.name }
            )
        }

    }
}
