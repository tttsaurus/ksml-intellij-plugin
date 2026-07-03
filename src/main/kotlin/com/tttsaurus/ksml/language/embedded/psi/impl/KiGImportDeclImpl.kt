package com.tttsaurus.ksml.language.embedded.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.tttsaurus.ksml.language.embedded.psi.KiGImportDecl

class KiGImportDeclImpl(
    node: ASTNode
) : ASTWrapperPsiElement(node), KiGImportDecl {
}
