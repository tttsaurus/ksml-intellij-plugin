package com.tttsaurus.ksml.language.embedded.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.tttsaurus.ksml.language.embedded.KiGFileElementType
import com.tttsaurus.ksml.language.embedded.KiGTypes
import com.tttsaurus.ksml.language.embedded.lexer.KiGLexer
import com.tttsaurus.ksml.language.embedded.psi.impl.KiGIdentDeclImpl
import com.tttsaurus.ksml.language.embedded.psi.impl.KiGImportDeclImpl

class KiGParserDefinition : ParserDefinition {

    override fun createLexer(p0: Project): Lexer {
        return KiGLexer()
    }

    override fun createParser(project: Project): PsiParser {
        return PsiParser { root, builder ->
            val fileMarker = builder.mark()

            while (!builder.eof()) {
                when (builder.tokenType) {
                    KiGTypes.IDENT -> {
                        val m = builder.mark()
                        builder.advanceLexer()
                        m.done(KiGTypes.IDENT)
                    }

                    KiGTypes.IMPORT -> {
                        val m = builder.mark()
                        builder.advanceLexer()
                        m.done(KiGTypes.IMPORT)
                    }

                    else -> builder.advanceLexer()
                }
            }

            fileMarker.done(root)
            builder.treeBuilt
        }
    }

    override fun getFileNodeType(): IFileElementType =
        KiGFileElementType.INSTANCE

    override fun getWhitespaceTokens(): TokenSet =
        TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens(): TokenSet =
        TokenSet.create(KiGTypes.COMMENT)

    override fun getStringLiteralElements(): TokenSet =
        TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement {
        return when (node.elementType) {
            KiGTypes.IMPORT -> KiGImportDeclImpl(node)
            KiGTypes.IDENT  -> KiGIdentDeclImpl(node)
            else -> throw AssertionError("Unknown element type: ${node.elementType}")
        }
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return object : PsiFileImpl(KiGFileElementType.INSTANCE, KiGFileElementType.INSTANCE, viewProvider) {
            override fun getFileType() = viewProvider.virtualFile.fileType
            override fun accept(visitor: PsiElementVisitor) {
            }
        }
    }
}
