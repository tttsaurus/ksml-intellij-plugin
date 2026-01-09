package com.tttsaurus.ksml.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.EmptyLexer
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.tttsaurus.ksml.KsmlFileElementType

class KsmlParserDefinition : ParserDefinition {
    override fun createLexer(p0: Project): Lexer {
        return EmptyLexer()
    }

    override fun createParser(p0: Project): PsiParser {
        return PsiParser { root, builder ->
            val marker = builder.mark()

            while (!builder.eof()) {
                builder.advanceLexer()
            }

            marker.done(root)
            builder.treeBuilt
        }
    }

    override fun getFileNodeType(): IFileElementType =
        KsmlFileElementType

    override fun getCommentTokens(): TokenSet =
        TokenSet.EMPTY

    override fun getStringLiteralElements(): TokenSet =
        TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement {
        return node.psi;
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return object : PsiFileImpl(KsmlFileElementType, KsmlFileElementType, viewProvider) {
            override fun getFileType() = viewProvider.virtualFile.fileType
            override fun accept(visitor: PsiElementVisitor) {
            }
        }
    }
}
