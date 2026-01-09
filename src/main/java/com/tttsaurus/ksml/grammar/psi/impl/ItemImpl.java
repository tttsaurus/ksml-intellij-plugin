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

public class ItemImpl extends ASTWrapperPsiElement implements Item {

  public ItemImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitItem(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GlslChunk getGlslChunk() {
    return findChildByClass(GlslChunk.class);
  }

  @Override
  @Nullable
  public GlslFunction getGlslFunction() {
    return findChildByClass(GlslFunction.class);
  }

  @Override
  @Nullable
  public GlslStruct getGlslStruct() {
    return findChildByClass(GlslStruct.class);
  }

  @Override
  @Nullable
  public KsmlAnnotation getKsmlAnnotation() {
    return findChildByClass(KsmlAnnotation.class);
  }

}
