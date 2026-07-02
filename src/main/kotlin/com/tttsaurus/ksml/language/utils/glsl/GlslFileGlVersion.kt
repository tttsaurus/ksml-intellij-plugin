package com.tttsaurus.ksml.language.utils.glsl

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

object GlslFileGlVersion {

    data class GlVersion(
        val version: Int,
        val versionRange: TextRange,
        val ident: String?,
        val identRange: TextRange?
    )

    private val VERSION_REGEX =
        Regex("""#version\s+(\d+)(?:\s+([a-zA-Z_][a-zA-Z0-9_]*))?""")

    fun getGlVersion(file: PsiFile): GlVersion? {
        return CachedValuesManager.getCachedValue(file) {
            val text = file.text

            val match = VERSION_REGEX.find(text)

            val result = match?.let {
                val versionGroup = it.groups[1]!!

                val profileGroup = it.groups[2]

                GlVersion(
                    version = versionGroup.value.toInt(),
                    versionRange = TextRange(
                        versionGroup.range.first,
                        versionGroup.range.last + 1
                    ),
                    ident = profileGroup?.value,
                    identRange = profileGroup?.let { g ->
                        TextRange(
                            g.range.first,
                            g.range.last + 1
                        )
                    }
                )
            }

            CachedValueProvider.Result.create(result, file)
        }
    }
}
