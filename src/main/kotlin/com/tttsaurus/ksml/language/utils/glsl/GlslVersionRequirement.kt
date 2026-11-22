package com.tttsaurus.ksml.language.utils.glsl

import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl

@ConsistentCopyVisibility
data class GlslVersionRequirement private constructor(val version: Int, val profile: Profile?) {

    enum class Profile {
        CORE,
        COMPATIBILITY
    }

    val displayName: String
        get() = when (profile) {
            Profile.CORE -> "GL$version CORE"
            Profile.COMPATIBILITY -> "GL$version COM"
            null -> "GL$version"
        }

    fun satisfies(required: GlslVersionRequirement): Boolean {
        if (version < required.version) return false
        if (version < 150 || required.version < 150) return true
        return required.profile == Profile.CORE || profile == Profile.COMPATIBILITY
    }

    companion object {
        val supportedVersions = setOf(110, 120, 130, 140, 150, 330, 400, 410, 420, 430, 440, 450, 460)

        fun validationError(version: Int, identifier: String?, ksml: Boolean = true): String? = when {
            version !in supportedVersions -> "version.unsupported"
            version < 150 && identifier != null -> "version.profileUnavailable"
            identifier != null && identifier !in setOf("core", "compatibility") && !(ksml && identifier == "compat") -> "version.invalidProfile"
            else -> null
        }

        fun resolve(version: Int?, identifier: String?, ksml: Boolean = true): GlslVersionRequirement? {
            if (version == null || validationError(version, identifier, ksml) != null) return null
            val profile = when {
                version < 150 -> null
                identifier == "compat" || identifier == "compatibility" -> Profile.COMPATIBILITY
                else -> Profile.CORE
            }
            return GlslVersionRequirement(version, profile)
        }

        fun of(function: KsmlCodeDecl): GlslVersionRequirement? {
            val override = function.funcGlVersion
            return if (override != null) resolve(override, function.funcGlVersionIdent)
            else resolve(function.moduleGlVersion, function.moduleGlVersionIdent)
        }
    }
}
