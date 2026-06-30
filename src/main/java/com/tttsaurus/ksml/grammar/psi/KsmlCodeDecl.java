// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.tttsaurus.ksml.language.psi.KsmlCodeDeclInterface;
import com.tttsaurus.ksml.language.metadata.KsmlCodeDeclMetadata;

public interface KsmlCodeDecl extends KsmlCodeDeclInterface {

  @NotNull
  PsiElement getCodeBlock();

  @Nullable
  PsiElement getEol();

  @Nullable String getFunctionName();

  @NotNull KsmlCodeDeclMetadata getMetadata();

}
