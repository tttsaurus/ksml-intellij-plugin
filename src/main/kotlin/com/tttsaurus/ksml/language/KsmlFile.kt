package com.tttsaurus.ksml.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import javax.swing.Icon

class KsmlFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, KsmlLanguage.INSTANCE) {
    override fun toString(): String {
        return viewProvider.virtualFile.name
    }

    override fun getFileType(): FileType {
        return KsmlFileTypeDef()
    }

    override fun getIcon(flags: Int): Icon {
        return KsmlIcons.FILE
    }
}
