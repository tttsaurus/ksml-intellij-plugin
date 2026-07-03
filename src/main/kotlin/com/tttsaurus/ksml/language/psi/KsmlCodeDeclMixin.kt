package com.tttsaurus.ksml.language.psi

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.stubs.IStubElementType
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.KsmlIcons
import com.tttsaurus.ksml.language.utils.ksml.KsmlCodeDeclMetadataParser
import com.tttsaurus.ksml.language.stub.KsmlCodeDeclStub
import com.tttsaurus.ksml.language.utils.glsl.GlslFunctionSignExtractor
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer

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
        // functionName shouldn't be null. parse manually
        return GlslFunctionSignExtractor.extractFuncNameFromCodeBlockTokenText(codeBlock.text)
    }

    override fun getModuleName(): String? {
        stub?.moduleName?.let { return it }
        // moduleName shouldn't be null. access from the file
        return (containingFile as KsmlFile).moduleName
    }

    override fun getModuleFileName(): String? {
        stub?.moduleFileName?.let { return it }
        // moduleFileName shouldn't be null. access from the file
        return (containingFile as KsmlFile).moduleFileName
    }

    override fun getModuleGlVersion(): Int? {
        stub?.moduleGlVersion?.let { return it }
        // moduleGlVersion shouldn't be null. access from the file
        return (containingFile as KsmlFile).moduleGlVersion
    }

    override fun getModuleGlVersionIdent(): String? {
        // moduleGlVersionIdent can be null
        stub?.let { return it.moduleGlVersionIdent }
        return (containingFile as KsmlFile).moduleGlVersionIdent
    }

    override fun getFuncGlVersion(): Int? {
        // funcGlVersion can be null
        stub?.let { return it.funcGlVersion }
        val metadata = KsmlCodeDeclMetadataParser.parse(node.startOffset, containingFile.text)
        return metadata.funcGlVersion
    }

    override fun getFuncGlVersionIdent(): String? {
        // funcGlVersionIdent can be null
        stub?.let { return it.funcGlVersionIdent }
        val metadata = KsmlCodeDeclMetadataParser.parse(node.startOffset, containingFile.text)
        return metadata.funcGlVersionIdent
    }

    override fun getIsExport(): Boolean {
        stub?.isExport?.let { return it }
        // isExport must not be null. parse manually
        val metadata = KsmlCodeDeclMetadataParser.parse(node.startOffset, containingFile.text)
        return metadata.isExport
    }

    override fun getFeatureRequired(): String? {
        // featureRequired can be null
        stub?.let { return it.featureRequired }
        val metadata = KsmlCodeDeclMetadataParser.parse(node.startOffset, containingFile.text)
        return metadata.featureRequired
    }

    override fun getParams(): List<String>? {
        // params can be null
        stub?.let { return it.params }
        return GlslFunctionSignExtractor.extractParamTypesFromCodeBlockTokenText(codeBlock.text)
    }

    override fun getReturnType(): String? {
        stub?.returnType?.let { return it }
        // returnType shouldn't be null. parse manually
        return GlslFunctionSignExtractor.extractReturnTypeFromCodeBlockTokenText(codeBlock.text)
    }

    override fun getPresentation(): ItemPresentation? {
        val moduleGlVersion = moduleGlVersion
        val functionGlVersion = funcGlVersion

        var glVersion: String? = null
        if (functionGlVersion != null) {
            glVersion = "GL$functionGlVersion${GlslProfileInferencer.getProfileDescSymbol(funcGlVersionIdent)}"
        } else if (moduleGlVersion != null) {
            glVersion = "GL$moduleGlVersion${GlslProfileInferencer.getProfileDescSymbol(moduleGlVersionIdent)}"
        }

        val title = StringBuilder()
        title.append("${returnType ?: "UNKNOWN_TYPE"} ${functionName ?: "UNKNOWN_FUNCTION"}")
        title.append("(")
        val params = params
        if (params != null) {
            for ((i, element) in params.withIndex()) {
                title.append(element)
                if (i < params.size - 1) {
                    title.append(", ")
                }
            }
        }
        title.append(")")

        val subTitle = StringBuilder()
        if (glVersion != null) {
            subTitle.append(glVersion).append(" | ")
        }
        subTitle.append(if (isExport) "exported" else "internal").append(" | ")
        subTitle.append("from ${moduleName ?: "UNKNOWN_MODULE"} | ${moduleFileName ?: "UNKNOWN_FILE"}")

        return object : ItemPresentation {
            override fun getPresentableText() = title.toString()

            override fun getLocationString() = subTitle.toString()

            override fun getIcon(unused: Boolean) = KsmlIcons.FILE
        }
    }
}
