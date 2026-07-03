package com.tttsaurus.ksml.language.editor

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.tttsaurus.ksml.language.KsmlFile

class KsmlTypedHandlerDelegate : TypedHandlerDelegate() {

    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is KsmlFile) return Result.CONTINUE

        val document = editor.document
        val offset = editor.caretModel.offset

        if (c == '"' && offset >= 3) {
            val chars = document.charsSequence

            if (chars[offset - 1] == '"' &&
                chars[offset - 2] == '"' &&
                chars[offset - 3] == '"'
            ) {
                document.insertString(offset, "\"\"\"")
                editor.caretModel.moveToOffset(offset)
                return Result.STOP
            }
        }

        return Result.CONTINUE
    }
}
