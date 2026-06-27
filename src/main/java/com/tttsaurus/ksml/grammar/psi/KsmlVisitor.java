// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;

public class KsmlVisitor extends PsiElementVisitor {

  public void visitCodeComment(@NotNull KsmlCodeComment o) {
    visitPsiElement(o);
  }

  public void visitCodeDecl(@NotNull KsmlCodeDecl o) {
    visitPsiLanguageInjectionHost(o);
  }

  public void visitExportDecl(@NotNull KsmlExportDecl o) {
    visitPsiElement(o);
  }

  public void visitFeatureDecl(@NotNull KsmlFeatureDecl o) {
    visitPsiElement(o);
  }

  public void visitGlRequiresDecl(@NotNull KsmlGlRequiresDecl o) {
    visitPsiElement(o);
  }

  public void visitGlVersionDecl(@NotNull KsmlGlVersionDecl o) {
    visitPsiElement(o);
  }

  public void visitItem(@NotNull KsmlItem o) {
    visitPsiElement(o);
  }

  public void visitKsmlAnnotation(@NotNull KsmlKsmlAnnotation o) {
    visitPsiElement(o);
  }

  public void visitModuleDecl(@NotNull KsmlModuleDecl o) {
    visitPsiElement(o);
  }

  public void visitRequiresDecl(@NotNull KsmlRequiresDecl o) {
    visitPsiElement(o);
  }

  public void visitPsiLanguageInjectionHost(@NotNull PsiLanguageInjectionHost o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
