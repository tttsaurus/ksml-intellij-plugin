package com.tttsaurus.ksml.language.stub

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.*
import com.intellij.util.io.StringRef
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.grammar.psi.KsmlTypes
import com.tttsaurus.ksml.grammar.psi.impl.KsmlCodeDeclImpl
import com.tttsaurus.ksml.language.KsmlLanguage
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY

object KsmlCodeDeclElementType :
    IStubElementType<KsmlCodeDeclStub, KsmlCodeDecl>("CODE_DECL", KsmlLanguage.INSTANCE) {

    override fun getExternalId(): String = "ksml.code_decl"

    override fun createPsi(stub: KsmlCodeDeclStub): KsmlCodeDecl {
        return KsmlCodeDeclImpl(stub, this)
    }

    override fun createStub(psi: KsmlCodeDecl, parentStub: StubElement<*>?): KsmlCodeDeclStub {
        val name = extractFunctionName(psi.node)
        return KsmlCodeDeclStubImpl(parentStub, this, name)
    }

    override fun shouldCreateStub(node: ASTNode): Boolean {
        return extractFunctionName(node) != null
    }

    override fun serialize(stub: KsmlCodeDeclStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.functionName)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KsmlCodeDeclStub {
        val name = StringRef.toString(dataStream.readName())
        return KsmlCodeDeclStubImpl(parentStub, this, name)
    }

    override fun indexStub(stub: KsmlCodeDeclStub, sink: IndexSink) {
        val name = stub.functionName ?: return
        sink.occurrence(FUNCTION_INDEX_KEY, name)
    }

    private fun extractFunctionName(node: ASTNode): String? {
        val codeBlock = node.findChildByType(KsmlTypes.CODE_BLOCK) ?: return null
        return KsmlFunctionNameExtractor.extractFromCodeBlockTokenText(codeBlock.text)
    }
}
