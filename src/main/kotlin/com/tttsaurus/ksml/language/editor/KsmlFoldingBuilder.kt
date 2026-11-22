package com.tttsaurus.ksml.language.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.grammar.psi.KsmlTypes
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionRequirement
import kotlin.collections.withIndex

class KsmlFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(
        root: PsiElement,
        document: Document,
        bool: Boolean
    ): Array<out FoldingDescriptor?> {

        val result = mutableListOf<FoldingDescriptor>()

        PsiTreeUtil.findChildrenOfType(root, KsmlCodeDecl::class.java).forEach {
            result += FoldingDescriptor(
                it.codeBlock.node,
                it.codeBlock.textRange,
                null,
                setOf(root.containingFile)
            )
        }

        return result.toTypedArray()
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false

    override fun getPlaceholderText(node: ASTNode): String? {
        return when (node.elementType) {
            KsmlTypes.CODE_BLOCK -> {
                val title = StringBuilder()
                val codeDecl = node.psi.parent as? KsmlCodeDecl ?: return "..."

                val glVersion = GlslVersionRequirement.of(codeDecl)?.displayName

                if (glVersion != null) {
                    title.append(glVersion).append(" | ")
                }

                title.append("${codeDecl.returnType ?: "UNKNOWN_TYPE"} ${codeDecl.functionName ?: "UNKNOWN_FUNCTION"}")
                title.append("(")
                val params = codeDecl.params
                if (params != null) {
                    for ((i, element) in params.withIndex()) {
                        title.append(element)
                        if (i < params.size - 1) {
                            title.append(", ")
                        }
                    }
                }
                title.append(") { ... }")

                title.toString()
            }

            else -> "..."
        }
    }
}
