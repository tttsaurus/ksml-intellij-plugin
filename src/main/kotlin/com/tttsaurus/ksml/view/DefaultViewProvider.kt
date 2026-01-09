package com.tttsaurus.ksml.view

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.SingleRootFileViewProvider

class DefaultViewProvider(
    manager: PsiManager,
    virtualFile: VirtualFile,
    eventSystemEnabled: Boolean,
    baseLanguage: Language
) : SingleRootFileViewProvider(manager, virtualFile, eventSystemEnabled, baseLanguage)
