package com.tttsaurus.ksml.language.embed_lang.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.tttsaurus.ksml.language.embed_lang.KiGFileElementType
import com.tttsaurus.ksml.language.embed_lang.KiGTypes
import com.tttsaurus.ksml.language.embed_lang.lexer.KiGLexer
import com.tttsaurus.ksml.language.embed_lang.psi.impl.KiGIdentDeclImpl
import com.tttsaurus.ksml.language.embed_lang.psi.impl.KiGImportDeclImpl

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

    override fun getCommentTokens(): TokenSet =
        TokenSet.EMPTY

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
