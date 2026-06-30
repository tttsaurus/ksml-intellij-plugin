package com.tttsaurus.ksml.language.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.grammar.psi.KsmlGlVersionDecl
import com.tttsaurus.ksml.grammar.psi.KsmlTypes
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.KsmlIcons

abstract class KsmlModuleDeclMixin : ASTWrapperPsiElement {

    constructor(node: ASTNode) : super(node)

    override fun getPresentation(): ItemPresentation? {
        val file = containingFile as KsmlFile
        val glVersion = PsiTreeUtil.findChildOfType(
            file,
            KsmlGlVersionDecl::class.java
        )

        val number = glVersion?.number?.text
        val identifier = glVersion?.identifier?.text

        val subTitle = StringBuilder()
        subTitle.append(file.name)
        if (number != null) {
            subTitle.append(" - GL").append(number)
            if (identifier != null) {
                subTitle.append(" ").append(identifier)
            }
        }

        val ident = PsiTreeUtil.findChildrenOfType(this, PsiElement::class.java)
            .firstOrNull {
                it.node?.elementType == KsmlTypes.IDENTIFIER
            }
        val title = ident?.text

        return object : ItemPresentation {
            override fun getPresentableText() = title

            override fun getLocationString() = subTitle.toString()

            override fun getIcon(unused: Boolean) = KsmlIcons.FILE
        }
    }
}
