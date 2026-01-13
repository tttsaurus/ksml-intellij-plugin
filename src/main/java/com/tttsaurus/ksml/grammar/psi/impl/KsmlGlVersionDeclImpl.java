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

public class KsmlGlVersionDeclImpl extends ASTWrapperPsiElement implements KsmlGlVersionDecl {

  public KsmlGlVersionDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull KsmlVisitor visitor) {
    visitor.visitGlVersionDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof KsmlVisitor) accept((KsmlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getEol() {
    return findNotNullChildByType(EOL);
  }

  @Override
  @Nullable
  public PsiElement getIdentifier() {
    return findChildByType(IDENTIFIER);
  }

  @Override
  @NotNull
  public PsiElement getNumber() {
    return findNotNullChildByType(NUMBER);
  }

}
