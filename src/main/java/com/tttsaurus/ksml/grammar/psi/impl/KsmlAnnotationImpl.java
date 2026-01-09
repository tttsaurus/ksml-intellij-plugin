// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.tttsaurus.ksml.grammar.psi.KsmlTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.tttsaurus.ksml.grammar.psi.*;

public class KsmlAnnotationImpl extends ASTWrapperPsiElement implements KsmlAnnotation {

  public KsmlAnnotationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitKsmlAnnotation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ExportDecl getExportDecl() {
    return findChildByClass(ExportDecl.class);
  }

  @Override
  @Nullable
  public FeatureDecl getFeatureDecl() {
    return findChildByClass(FeatureDecl.class);
  }

  @Override
  @Nullable
  public GlRequiresDecl getGlRequiresDecl() {
    return findChildByClass(GlRequiresDecl.class);
  }

  @Override
  @Nullable
  public GlVersionDecl getGlVersionDecl() {
    return findChildByClass(GlVersionDecl.class);
  }

  @Override
  @Nullable
  public ModuleDecl getModuleDecl() {
    return findChildByClass(ModuleDecl.class);
  }

  @Override
  @Nullable
  public RequiresDecl getRequiresDecl() {
    return findChildByClass(RequiresDecl.class);
  }

}
