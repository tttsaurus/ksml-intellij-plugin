package com.tttsaurus.ksml.language.utils.glsl

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.tttsaurus.ksml.settings.KsmlProjectSettings

object GlslProfileInferencer {

    enum class GlProfile {
        CORE,
        COMPATIBLE
    }

    private const val CORE_SYMBOL = "C"
    private const val COMPAT_SYMBOL = ""

    fun getDefaultProfile(project: Project): GlProfile {
        return project.service<KsmlProjectSettings>().defaultGlslProfile
    }

    fun getProfileDescSymbol(project: Project, identifier: String?): String {
        return getProfileDescSymbol(getProfile(project, identifier))
    }

    fun getProfileDescSymbol(profile: GlProfile): String {
        return when (profile) {
            GlProfile.CORE -> CORE_SYMBOL
            GlProfile.COMPATIBLE -> COMPAT_SYMBOL
        }
    }

    fun getProfile(project: Project, identifier: String?): GlProfile {
        return when (identifier) {
            "core" -> GlProfile.CORE
            "compat", "compatibility" -> GlProfile.COMPATIBLE
            else -> getDefaultProfile(project)
        }
    }

    fun compareProfiles(
        project: Project,
        versionA: Int,
        versionIdentA: String?,
        versionB: Int,
        versionIdentB: String?
    ): Int {
        if (versionA != versionB) {
            return versionA.compareTo(versionB)
        }

        val profileA = getProfile(project, versionIdentA)
        val profileB = getProfile(project, versionIdentB)

        if (profileA == profileB) {
            return 0
        }

        // B covers A
        if (profileA == GlProfile.CORE && profileB == GlProfile.COMPATIBLE) {
            return -1
        }

        // A covers B
        if (profileA == GlProfile.COMPATIBLE && profileB == GlProfile.CORE) {
            return 1
        }

        return 0
    }
}
