package com.tttsaurus.ksml.language.utils.ksml

import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile

object KsmlCodeDeclLocationDataParser {

    fun parse(file: KsmlFile): List<KsmlCodeDeclLocationData> {
        val result = mutableListOf<KsmlCodeDeclLocationData>()

        PsiTreeUtil.findChildrenOfType(file, KsmlCodeDecl::class.java).forEach {
            result += KsmlCodeDeclLocationData(
                it.functionName,
                it.textRange
            )
        }

        return result
    }
}
