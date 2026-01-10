package com.tttsaurus.ksml.language.embed_lang.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.tttsaurus.ksml.language.embed_lang.psi.KiGIdentDecl

class KiGIdentDeclImpl(
    node: ASTNode
) : ASTWrapperPsiElement(node), KiGIdentDecl {
}
