package com.tttsaurus.ksml.language.psi

import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.StubBasedPsiElement
import com.tttsaurus.ksml.language.stub.KsmlCodeDeclStub

interface KsmlCodeDeclInterface :
    PsiLanguageInjectionHost,
    StubBasedPsiElement<KsmlCodeDeclStub?>,
    PsiNamedElement
