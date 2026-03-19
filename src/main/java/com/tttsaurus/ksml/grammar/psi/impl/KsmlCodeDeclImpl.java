// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.tttsaurus.ksml.grammar.psi.KsmlTypes.*;
import com.tttsaurus.ksml.language.parser.KsmlCodeDeclMixin;
import com.tttsaurus.ksml.grammar.psi.*;

public class KsmlCodeDeclImpl extends KsmlCodeDeclMixin implements KsmlCodeDecl {

  public KsmlCodeDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull KsmlVisitor visitor) {
    visitor.visitCodeDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof KsmlVisitor) accept((KsmlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getCodeBlock() {
    return findNotNullChildByType(CODE_BLOCK);
  }

  @Override
  @Nullable
  public PsiElement getEol() {
    return findChildByType(EOL);
  }

}
