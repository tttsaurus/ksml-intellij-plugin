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

public class ModuleDeclImpl extends ASTWrapperPsiElement implements ModuleDecl {

  public ModuleDeclImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitModuleDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

}
