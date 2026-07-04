package com.tttsaurus.ksml.language.navigation

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.Processor
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.navigation.usage.FunctionUsageEntrypoint

class KsmlCodeDeclReferenceSearcher :
    QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>() {

    override fun processQuery(
        params: ReferencesSearch.SearchParameters,
        processor: Processor<in PsiReference>
    ) {
        val codeDecl = params.elementToSearch as? KsmlCodeDecl ?: return

        ReadAction.run<RuntimeException> {
            val module = codeDecl.moduleName ?: return@run
            val function = codeDecl.functionName ?: return@run

            FunctionUsageEntrypoint.getFunctionUsages(
                codeDecl.project,
                module,
                function
            ).forEach {
                for (ref in FunctionUsageEntrypoint.findReferencesViaFunctionUsage(codeDecl, it)) {
                    processor.process(ref)
                }
            }
        }
    }
}
