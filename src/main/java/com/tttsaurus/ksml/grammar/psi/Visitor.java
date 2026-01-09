// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;

public class Visitor extends PsiElementVisitor {

  public void visitAnyChunk(@NotNull AnyChunk o) {
    visitPsiLanguageInjectionHost(o);
  }

  public void visitExportDecl(@NotNull ExportDecl o) {
    visitPsiElement(o);
  }

  public void visitFeatureDecl(@NotNull FeatureDecl o) {
    visitPsiElement(o);
  }

  public void visitGlRequiresDecl(@NotNull GlRequiresDecl o) {
    visitPsiElement(o);
  }

  public void visitGlVersionDecl(@NotNull GlVersionDecl o) {
    visitPsiElement(o);
  }

  public void visitItem(@NotNull Item o) {
    visitPsiElement(o);
  }

  public void visitKsmlAnnotation(@NotNull KsmlAnnotation o) {
    visitPsiElement(o);
  }

  public void visitModuleDecl(@NotNull ModuleDecl o) {
    visitPsiElement(o);
  }

  public void visitRequiresDecl(@NotNull RequiresDecl o) {
    visitPsiElement(o);
  }

  public void visitPsiLanguageInjectionHost(@NotNull PsiLanguageInjectionHost o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
