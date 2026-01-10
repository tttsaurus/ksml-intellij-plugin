package com.tttsaurus.ksml.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement

class KsmlTestAnnotator : Annotator {

    private val LOGGER : Logger = Logger.getInstance(KsmlTestAnnotator::class.java)

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

    }
}
