package com.tttsaurus.ksml.language.psi

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.startOffset
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.utils.metadata.KsmlCodeDeclMetadataParser
import com.tttsaurus.ksml.language.stub.KsmlCodeDeclStub
import com.tttsaurus.ksml.language.utils.GlslFunctionSignExtractor

abstract class KsmlCodeDeclMixin :
    StubBasedPsiElementBase<KsmlCodeDeclStub>,
    PsiLanguageInjectionHost,
    KsmlCodeDecl {

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

    override fun getFunctionName(): String? {
        stub?.functionName?.let { return it }
        return GlslFunctionSignExtractor.extractFuncNameFromCodeBlockTokenText(codeBlock.text)
    }

    override fun getModuleName(): String? {
        stub?.moduleName?.let { return it }
        return (containingFile as KsmlFile).moduleName
    }

    override fun getModuleFileName(): String? {
        stub?.moduleFileName?.let { return it }
        return (containingFile as KsmlFile).moduleFileName
    }

    override fun getModuleGlVersion(): Int? {
        stub?.moduleGlVersion?.let { return it }
        return (containingFile as KsmlFile).moduleGlVersion
    }

    override fun getModuleGlVersionIdent(): String? {
        stub?.moduleGlVersionIdent?.let { return it }
        return (containingFile as KsmlFile).moduleGlVersionIdent
    }

    override fun getFuncGlVersion(): Int? {
        stub?.moduleGlVersion?.let { return it }
        val metadata = KsmlCodeDeclMetadataParser.parse(codeBlock.startOffset, containingFile.text)
        return metadata.funcGlVersion
    }

    override fun getFuncGlVersionIdent(): String? {
        stub?.moduleGlVersionIdent?.let { return it }
        val metadata = KsmlCodeDeclMetadataParser.parse(codeBlock.startOffset, containingFile.text)
        return metadata.funcGlVersionIdent
    }

    override fun getIsExport(): Boolean {
        stub?.isExport?.let { return it }
        val metadata = KsmlCodeDeclMetadataParser.parse(codeBlock.startOffset, containingFile.text)
        return metadata.isExport
    }

    override fun getFeatureRequired(): String? {
        stub?.featureRequired?.let { return it }
        val metadata = KsmlCodeDeclMetadataParser.parse(codeBlock.startOffset, containingFile.text)
        return metadata.featureRequired
    }

    override fun getParams(): List<String>? {
        stub?.params?.let { return it }
        return GlslFunctionSignExtractor.extractParamTypesFromCodeBlockTokenText(codeBlock.text)
    }
}
