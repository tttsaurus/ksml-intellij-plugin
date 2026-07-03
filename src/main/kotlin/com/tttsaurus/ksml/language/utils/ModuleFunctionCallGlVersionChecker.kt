package com.tttsaurus.ksml.language.utils

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.utils.glsl.GlslFileGlVersion
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer
import com.tttsaurus.ksml.language.utils.ksml.KsmlCodeDeclMetadataParser
import com.tttsaurus.ksml.language.utils.ksml.KsmlModuleMetadataParser

object ModuleFunctionCallGlVersionChecker {

    data class Payload(
        val requiredGlVersionString: String,
        val envGlVersionString: String
    )

    private val fallbackResult = Pair(true, null)

    fun doesFileHaveRequiredGlVersion(
        project: Project,
        functionCallInitiatorFile: PsiFile,
        targetCodeDecl: KsmlCodeDecl
    ): Pair<Boolean, Payload?> {

        val langInjectionManager = InjectedLanguageManager.getInstance(project)

        var fileGlVersion: Int? = null
        var fileGlVersionIdent: String? = null

        if (langInjectionManager.isInjectedFragment(functionCallInitiatorFile)) {
            val host = langInjectionManager.getInjectionHost(functionCallInitiatorFile) ?: return fallbackResult
            val hostFile = host.containingFile ?: return fallbackResult
            if (hostFile !is KsmlFile) return fallbackResult

            val metadata = KsmlModuleMetadataParser.parse(hostFile)
            fileGlVersion = metadata.glVersion
            fileGlVersionIdent = metadata.glVersionIdent
        } else {
            val ver = GlslFileGlVersion.getGlVersion(functionCallInitiatorFile) ?: return fallbackResult
            fileGlVersion = ver.version
            fileGlVersionIdent = ver.ident
        }

        if (fileGlVersion == null) return fallbackResult

        val ksmlFile = targetCodeDecl.containingFile as KsmlFile

        val moduleMetadata = KsmlModuleMetadataParser.parse(ksmlFile)
        val codeDeclMetadata = KsmlCodeDeclMetadataParser.parse(targetCodeDecl.node.startOffset, ksmlFile.text)

        var requiredGlVersion: Int? = null
        var requiredGlVersionIdent: String? = null
        if (codeDeclMetadata.funcGlVersion != null) {
            requiredGlVersion = codeDeclMetadata.funcGlVersion
            requiredGlVersionIdent = codeDeclMetadata.funcGlVersionIdent
        } else if (moduleMetadata.glVersion != null) {
            requiredGlVersion = moduleMetadata.glVersion
            requiredGlVersionIdent = moduleMetadata.glVersionIdent
        }

        if (requiredGlVersion != null) {
            val compare = GlslProfileInferencer.compareProfiles(
                fileGlVersion,
                fileGlVersionIdent,
                requiredGlVersion,
                requiredGlVersionIdent
            )

            val target = "GL$requiredGlVersion${GlslProfileInferencer.getProfileDescSymbol(requiredGlVersionIdent)}"
            val got = "GL$fileGlVersion${GlslProfileInferencer.getProfileDescSymbol(fileGlVersionIdent)}"
            val payload = Payload(target, got)

            return if (compare < 0) {
                Pair(false, payload)
            } else {
                Pair(true, payload)
            }
        }

        return fallbackResult
    }
}
