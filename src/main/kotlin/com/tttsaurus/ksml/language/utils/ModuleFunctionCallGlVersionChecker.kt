package com.tttsaurus.ksml.language.utils

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.utils.glsl.GlslFileGlVersion
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionRequirement

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

        val provided = if (langInjectionManager.isInjectedFragment(functionCallInitiatorFile)) {
            val host = langInjectionManager.getInjectionHost(functionCallInitiatorFile) ?: return fallbackResult
            val hostFile = host.containingFile as? KsmlFile ?: return fallbackResult
            val caller = host as? KsmlCodeDecl
            if (caller != null) GlslVersionRequirement.of(caller)
            else GlslVersionRequirement.resolve(hostFile.moduleGlVersion, hostFile.moduleGlVersionIdent)
        } else {
            val version = GlslFileGlVersion.getGlVersion(functionCallInitiatorFile) ?: return fallbackResult
            GlslVersionRequirement.resolve(version.version, version.ident, ksml = false)
        } ?: return fallbackResult

        val required = GlslVersionRequirement.of(targetCodeDecl) ?: return fallbackResult
        return provided.satisfies(required) to Payload(required.displayName, provided.displayName)
    }
}
