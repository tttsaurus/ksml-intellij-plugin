package com.tttsaurus.ksml.language.utils.glsl

object GlslProfileInferencer {

    enum class GlProfile {
        CORE,
        COMPATIBLE
    }

    private const val CORE_SYMBOL = "C"
    private const val COMPAT_SYMBOL = ""

    fun getDefaultProfile(): GlProfile {
        return GlProfile.COMPATIBLE
    }

    fun getProfileDescSymbol(identifier: String?): String {
        return getProfileDescSymbol(getProfile(identifier))
    }

    fun getProfileDescSymbol(profile: GlProfile): String {
        return when (profile) {
            GlProfile.CORE -> CORE_SYMBOL
            GlProfile.COMPATIBLE -> COMPAT_SYMBOL
        }
    }

    fun getProfile(identifier: String?): GlProfile {
        return when (identifier) {
            "core" -> GlProfile.CORE
            "compat", "compatibility" -> GlProfile.COMPATIBLE
            null, "" -> getDefaultProfile()
            else -> getDefaultProfile()
        }
    }

    fun compareProfiles(
        versionA: Int,
        versionIdentA: String?,
        versionB: Int,
        versionIdentB: String?
    ): Int {
        if (versionA != versionB) {
            return versionA.compareTo(versionB)
        }

        val profileA = getProfile(versionIdentA)
        val profileB = getProfile(versionIdentB)

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
