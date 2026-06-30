package com.tttsaurus.ksml.language.stub

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.*
import com.intellij.util.io.StringRef
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.grammar.psi.KsmlTypes
import com.tttsaurus.ksml.grammar.psi.impl.KsmlCodeDeclImpl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.KsmlLanguage
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY

object KsmlCodeDeclElementType :
    IStubElementType<KsmlCodeDeclStub, KsmlCodeDecl>("CODE_DECL", KsmlLanguage.INSTANCE) {

    override fun getExternalId(): String = "ksml.code_decl"

    override fun createPsi(stub: KsmlCodeDeclStub): KsmlCodeDecl {
        return KsmlCodeDeclImpl(stub, this)
    }

    override fun createStub(psi: KsmlCodeDecl, parentStub: StubElement<*>?): KsmlCodeDeclStub {
        val ksmlFile = psi.containingFile as KsmlFile
        val metadata = psi.metadata
        val functionName = extractFunctionName(psi.node)
        val params = extractParamTypes(psi.node)

        return KsmlCodeDeclStubImpl(
            parentStub, this,
            functionName,
            ksmlFile.moduleName,
            ksmlFile.moduleFileName,
            ksmlFile.moduleGlVersion,
            ksmlFile.moduleGlVersionIdent,
            metadata.funcGlVersion,
            metadata.funcGlVersionIdent,
            metadata.isExport,
            metadata.featureRequired,
            params
        )
    }

    override fun shouldCreateStub(node: ASTNode): Boolean {
        return extractFunctionName(node) != null
    }

    override fun serialize(stub: KsmlCodeDeclStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.functionName)
        dataStream.writeName(stub.moduleName)
        dataStream.writeName(stub.moduleFileName)
        dataStream.writeVarInt(stub.moduleGlVersion ?: -1)
        dataStream.writeName(stub.moduleGlVersionIdent)
        dataStream.writeVarInt(stub.funcGlVersion ?: -1)
        dataStream.writeName(stub.funcGlVersionIdent)
        dataStream.writeBoolean(stub.isExport)
        dataStream.writeName(stub.featureRequired)
        if (stub.params == null) {
            dataStream.writeVarInt(-1)
        } else {
            dataStream.writeVarInt(stub.params!!.size)
            for (param in stub.params) {
                dataStream.writeName(param)
            }
        }
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KsmlCodeDeclStub {
        val functionName = StringRef.toString(dataStream.readName())
        val moduleName = StringRef.toString(dataStream.readName())
        val moduleFileName = StringRef.toString(dataStream.readName())
        val moduleGlVersion = dataStream.readVarInt()
        val moduleGlVersionIdent = StringRef.toString(dataStream.readName())
        val funcGlVersion = dataStream.readVarInt()
        val funcGlVersionIdent = StringRef.toString(dataStream.readName())
        val isExport = dataStream.readBoolean()
        val featureRequired = StringRef.toString(dataStream.readName())
        val paramCount = dataStream.readVarInt()
        var params: List<String>? = null
        if (paramCount != -1) {
            params = mutableListOf()
            for (i in 0 until paramCount) {
                val param = StringRef.toString(dataStream.readName())!!
                params.add(param)
            }
        }

        return KsmlCodeDeclStubImpl(
            parentStub, this,
            functionName,
            moduleName,
            moduleFileName,
            if (moduleGlVersion == -1) null else moduleGlVersion,
            moduleGlVersionIdent,
            if (funcGlVersion == -1) null else funcGlVersion,
            funcGlVersionIdent,
            isExport,
            featureRequired,
            params
        )
    }

    override fun indexStub(stub: KsmlCodeDeclStub, sink: IndexSink) {
        val name = stub.functionName ?: return
        sink.occurrence(FUNCTION_INDEX_KEY, name)
    }

    private fun extractFunctionName(node: ASTNode): String? {
        val codeBlock = node.findChildByType(KsmlTypes.CODE_BLOCK) ?: return null
        return KsmlFunctionSignExtractor.extractFromCodeBlockTokenText(codeBlock.text)
    }

    private fun extractParamTypes(node: ASTNode): List<String> {
        val codeBlock = node.findChildByType(KsmlTypes.CODE_BLOCK) ?: return emptyList()
        return KsmlFunctionSignExtractor.extractParamTypesFromCodeBlockTokenText(codeBlock.text)
    }
}
