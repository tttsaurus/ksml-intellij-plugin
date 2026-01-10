package com.tttsaurus.ksml

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class KsmlFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, KsmlLanguage.INSTANCE) {
    override fun toString(): String {
        return viewProvider.virtualFile.name
    }

    override fun getFileType(): FileType {
        return KsmlFileTypeDef()
    }
}