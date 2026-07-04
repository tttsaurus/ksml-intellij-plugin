package com.tttsaurus.ksml.language.utils.glsl

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.tttsaurus.ksml.language.GlslLanguage

object GlslPsiElementFactory {

    fun makeModuleFunctionCallFuncNameIdentifier(project: Project, functionName: String): PsiElement {
        val file = PsiFileFactory.getInstance(project).createFileFromText(
            GlslLanguage.GLSL_LANGUAGE,
            """
            void main() { a.$functionName(); }
            """.trimIndent()
        )
        val element = file.findElementAt(17)!!
        return element.parent!!
    }
}
