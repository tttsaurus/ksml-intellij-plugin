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

public class KsmlKsmlAnnotationImpl extends ASTWrapperPsiElement implements KsmlKsmlAnnotation {

  public KsmlKsmlAnnotationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull KsmlVisitor visitor) {
    visitor.visitKsmlAnnotation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof KsmlVisitor) accept((KsmlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public KsmlExportDecl getExportDecl() {
    return findChildByClass(KsmlExportDecl.class);
  }

  @Override
  @Nullable
  public KsmlFeatureDecl getFeatureDecl() {
    return findChildByClass(KsmlFeatureDecl.class);
  }

  @Override
  @Nullable
  public KsmlGlRequiresDecl getGlRequiresDecl() {
    return findChildByClass(KsmlGlRequiresDecl.class);
  }

  @Override
  @Nullable
  public KsmlGlVersionDecl getGlVersionDecl() {
    return findChildByClass(KsmlGlVersionDecl.class);
  }

  @Override
  @Nullable
  public KsmlModuleDecl getModuleDecl() {
    return findChildByClass(KsmlModuleDecl.class);
  }

  @Override
  @Nullable
  public KsmlRequiresDecl getRequiresDecl() {
    return findChildByClass(KsmlRequiresDecl.class);
  }

}
