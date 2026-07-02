package com.tttsaurus.ksml.language.embed_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.VisualPrefabs
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY
import com.tttsaurus.ksml.language.utils.GlslFileGlVersion
import com.tttsaurus.ksml.language.utils.GlslModuleCallParser
import com.tttsaurus.ksml.language.utils.GlslProfileInferencer
import com.tttsaurus.ksml.language.utils.ksml.KsmlCodeDeclMetadataParser
import com.tttsaurus.ksml.language.utils.ksml.KsmlModuleMetadataParser

class GlslModuleFuncCallAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return
        if (node.elementType.toString() != "VARIABLE_IDENTIFIER") return

        val text = element.text
        if (!Regex("[A-Za-z_][A-Za-z0-9_]*").matches(text)) return

        val parentText = element.parent?.text ?: ""
        if (!parentText.contains('(') && !parentText.contains(')')) return

        val ppp = element.parent?.parent?.parent ?: return
        val moduleCall = GlslModuleCallParser.parse(ppp.text) ?: return

        val def = findFunctionDef(
            moduleCall.moduleName,
            moduleCall.functionName,
            moduleCall.arguments,
            element.project
        )

        if (!def.isEmpty()) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element.textRange)
                .textAttributes(VisualPrefabs.MODULE_FUNC_CALL_HIGHLIGHT)
                .create()
            if (def.size > 1) {
                holder.newAnnotation(
                    HighlightSeverity.WARNING,
                    KsmlBundle.message("KsmlInGlsl.functionDefUnclear")
                )
                    .range(element.textRange)
                    .create()
            } else {
                val fileVersion = GlslFileGlVersion.getGlVersion(element.containingFile) ?: return
                val matchingDef = def[0]
                val ksmlFile = matchingDef.containingFile as KsmlFile

                val moduleMetadata = KsmlModuleMetadataParser.parse(ksmlFile)
                val codeDeclMetadata = KsmlCodeDeclMetadataParser.parse(matchingDef.node.startOffset, ksmlFile.text)

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
                        fileVersion.version,
                        fileVersion.ident,
                        requiredGlVersion,
                        requiredGlVersionIdent
                    )
                    if (compare < 0) {
                        val target = "GL$requiredGlVersion${GlslProfileInferencer.getProfileDescSymbol(requiredGlVersionIdent)}"
                        val got = "GL${fileVersion.version}${GlslProfileInferencer.getProfileDescSymbol(fileVersion.ident)}"
                        holder.newAnnotation(
                            HighlightSeverity.ERROR,
                            KsmlBundle.message("KsmlInGlsl.functionCallGlRequirementNotMet", target, got)
                        )
                            .range(element.textRange)
                            .create()
                    }
                }
            }
        } else {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                KsmlBundle.message("KsmlInGlsl.functionDefNotFound")
            )
                .range(element.textRange)
                .create()
        }
    }

    private fun findFunctionDef(
        moduleName: String,
        functionName: String,
        funcCallArgs: List<String>,
        project: Project
    ): List<KsmlCodeDecl> {

        if (project.isDisposed) return emptyList()
        if (DumbService.isDumb(project)) return emptyList()

        if (functionName.isEmpty()) return emptyList()

        val decls = StubIndex.getElements(
            FUNCTION_INDEX_KEY,
            functionName,
            project,
            GlobalSearchScope.projectScope(project),
            KsmlCodeDecl::class.java
        )

        val result = mutableListOf<KsmlCodeDecl>()

        decls.forEach {
            if (it.moduleName == moduleName) {
                if (it.params?.size == funcCallArgs.size) {
                    result += it
                }
            }
        }

        return result
    }
}
