package com.tttsaurus.ksml.language.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.KsmlFileElementType
import com.tttsaurus.ksml.grammar.psi.KsmlTypes

class KsmlParserDefinition : ParserDefinition {

    private val LOGGER : Logger = Logger.getInstance(KsmlParserDefinition::class.java)

    init {
        LOGGER.info("KsmlParserDefinition Created")
    }

    override fun createLexer(p0: Project): Lexer =
        KsmlLexerAdapter()

    override fun createParser(p0: Project): PsiParser =
        KsmlParserAdapter()

    override fun getFileNodeType(): IFileElementType =
        KsmlFileElementType.INSTANCE

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
        return KsmlFile(viewProvider)
    }
}
