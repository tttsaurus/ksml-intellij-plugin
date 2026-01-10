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

public class KsmlItemImpl extends ASTWrapperPsiElement implements KsmlItem {

  public KsmlItemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull KsmlVisitor visitor) {
    visitor.visitItem(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof KsmlVisitor) accept((KsmlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public KsmlCodeComment getCodeComment() {
    return findChildByClass(KsmlCodeComment.class);
  }

  @Override
  @Nullable
  public KsmlKsmlAnnotation getKsmlAnnotation() {
    return findChildByClass(KsmlKsmlAnnotation.class);
  }

  @Override
  @Nullable
  public PsiElement getEol() {
    return findChildByType(EOL);
  }

  @Override
  @Nullable
  public PsiElement getWhiteSpace() {
    return findChildByType(WHITE_SPACE);
  }

}
