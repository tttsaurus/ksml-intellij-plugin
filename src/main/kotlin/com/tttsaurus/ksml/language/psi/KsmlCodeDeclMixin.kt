package com.tttsaurus.ksml.language.psi

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.stubs.IStubElementType
import com.tttsaurus.ksml.grammar.psi.KsmlTypes
import com.tttsaurus.ksml.language.stub.KsmlCodeDeclStub
import com.tttsaurus.ksml.language.stub.KsmlFunctionNameExtractor

abstract class KsmlCodeDeclMixin :
    StubBasedPsiElementBase<KsmlCodeDeclStub>,
    PsiLanguageInjectionHost {

    constructor(node: ASTNode) : super(node)

    constructor(stub: KsmlCodeDeclStub, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    override fun isValidHost() = true

    override fun updateText(text: String): PsiLanguageInjectionHost = this

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> =
        object : LiteralTextEscaper<PsiLanguageInjectionHost>(this@KsmlCodeDeclMixin) {
            override fun isOneLine(): Boolean = false

            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                outChars.append(rangeInsideHost.substring(myHost.text))
                return true
            }

            override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
                val offset = rangeInsideHost.startOffset + offsetInDecoded
                return minOf(offset, rangeInsideHost.endOffset)
            }
        }

    fun getFunctionName(): String? {
        stub?.functionName?.let { return it }

        val codeBlock = node.findChildByType(KsmlTypes.CODE_BLOCK)
            ?: return null

        return KsmlFunctionNameExtractor.extractFromCodeBlockTokenText(codeBlock.text)
    }
}
