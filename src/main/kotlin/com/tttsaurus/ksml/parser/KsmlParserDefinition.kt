package com.tttsaurus.ksml.parser

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.tttsaurus.ksml.KsmlFileElementType
import com.tttsaurus.ksml.KsmlLanguage
import com.tttsaurus.ksml.grammar.KsmlParser
import com.tttsaurus.ksml.grammar.psi.KsmlTypes

class KsmlParserDefinition : ParserDefinition {

    override fun createLexer(p0: Project): Lexer =
        KsmlLexerAdapter()

    override fun createParser(p0: Project): PsiParser =
        KsmlParser()

    override fun getFileNodeType(): IFileElementType =
        KsmlFileElementType

    override fun getWhitespaceTokens(): TokenSet =
        TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens(): TokenSet =
        TokenSet.create(KsmlTypes.COMMENT)

    override fun getStringLiteralElements(): TokenSet =
        TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement {
        return KsmlTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return object : PsiFileBase(viewProvider, KsmlLanguage) {
            override fun getFileType() = FileTypeManager.getInstance().getFileTypeByExtension("ksml")
        }
    }
}
