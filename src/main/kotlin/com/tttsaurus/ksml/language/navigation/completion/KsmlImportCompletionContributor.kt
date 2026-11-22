package com.tttsaurus.ksml.language.navigation.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiComment
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.language.GlslLanguage
import com.tttsaurus.ksml.language.KsmlIcons
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.utils.glsl.GlslFileModuleImports

class KsmlImportCompletionContributor : CompletionContributor() {

    override fun fillCompletionVariants(params: CompletionParameters, result: CompletionResultSet) {
        val original = params.originalFile
        val text = original.text
        if (params.offset !in 0..text.length) return
        val prefix = text.substring(0, params.offset).substringAfterLast('\n')
        if (!Regex("""^[\t ]*//[\t ]*@import[\t ]+[A-Za-z_0-9]*$""").matches(prefix)) return

        val manager = InjectedLanguageManager.getInstance(original.project)
        val host = manager.getInjectionHost(original) ?: PsiTreeUtil.getParentOfType(
            original.findElementAt((params.offset - 1).coerceAtLeast(0)), PsiComment::class.java, false
        ) ?: return
        if (host !is PsiComment || host.containingFile.language != GlslLanguage.GLSL_LANGUAGE) return

        val imported = GlslFileModuleImports.getImportedModules(host.containingFile)
            .filter { !host.textRange.contains(it.range) }
            .map { it.moduleName }
            .toSet()

        SymbolIndexEntrypoint.getAllModules(host.project).filter { it !in imported }.sorted().forEach {
            result.addElement(LookupElementBuilder.create(it).withIcon(KsmlIcons.FILE))
        }
    }
}
