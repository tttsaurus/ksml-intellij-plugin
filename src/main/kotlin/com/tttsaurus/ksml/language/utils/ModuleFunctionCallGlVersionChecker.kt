package com.tttsaurus.ksml.language.utils

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.utils.glsl.GlslFileGlVersion
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer

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

            val caller = host as? KsmlCodeDecl
            fileGlVersion = caller?.funcGlVersion ?: hostFile.moduleGlVersion
            fileGlVersionIdent = if (caller?.funcGlVersion != null) caller.funcGlVersionIdent else hostFile.moduleGlVersionIdent
        } else {
            val ver = GlslFileGlVersion.getGlVersion(functionCallInitiatorFile) ?: return fallbackResult
            fileGlVersion = ver.version
            fileGlVersionIdent = ver.ident
        }

        if (fileGlVersion == null) return fallbackResult

        val functionVersion = targetCodeDecl.funcGlVersion
        val requiredGlVersion = functionVersion ?: targetCodeDecl.moduleGlVersion
        val requiredGlVersionIdent = if (functionVersion != null) {
            targetCodeDecl.funcGlVersionIdent
        } else {
            targetCodeDecl.moduleGlVersionIdent
        }

        if (requiredGlVersion != null) {
            val compare = GlslProfileInferencer.compareProfiles(
                project,
                fileGlVersion,
                fileGlVersionIdent,
                requiredGlVersion,
                requiredGlVersionIdent
            )

            val target = "GL$requiredGlVersion${GlslProfileInferencer.getProfileDescSymbol(project, requiredGlVersionIdent)}"
            val got = "GL$fileGlVersion${GlslProfileInferencer.getProfileDescSymbol(project, fileGlVersionIdent)}"
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
