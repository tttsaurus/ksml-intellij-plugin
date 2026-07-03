package com.tttsaurus.ksml.language.editor

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.tttsaurus.ksml.language.KsmlFile

class KsmlEnterHandlerDelegate : EnterHandlerDelegateAdapter() {

    override fun postProcessEnter(
        file: PsiFile,
        editor: Editor,
        dataContext: DataContext
    ): EnterHandlerDelegate.Result {

        if (file !is KsmlFile) return EnterHandlerDelegate.Result.Continue

        val document = editor.document
        val caret = editor.caretModel.offset

        val line = document.getLineNumber(caret)

        if (line == 0) {
            return EnterHandlerDelegate.Result.Continue
        }

        val previous = lineText(document, line - 1).trim()
        if (!previous.endsWith("\"\"\"")) {
            return EnterHandlerDelegate.Result.Continue
        }

        val current = lineText(document, line).trim()
        if (current != "\"\"\"") {
            return EnterHandlerDelegate.Result.Continue
        }

        document.insertString(caret, "\n")
        editor.caretModel.moveToOffset(caret)

        return EnterHandlerDelegate.Result.Stop
    }

    private fun lineText(document: Document, line: Int): String {
        val start = document.getLineStartOffset(line)
        val end = document.getLineEndOffset(line)
        return document.getText(TextRange(start, end))
    }
}
