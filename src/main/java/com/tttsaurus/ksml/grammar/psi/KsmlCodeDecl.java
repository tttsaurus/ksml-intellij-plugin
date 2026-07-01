// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.tttsaurus.ksml.language.psi.KsmlCodeDeclInterface;

public interface KsmlCodeDecl extends KsmlCodeDeclInterface {

  @NotNull
  PsiElement getCodeBlock();

  @Nullable
  PsiElement getEol();

  @Nullable String getFunctionName();

  @Nullable String getModuleName();

  @Nullable String getModuleFileName();

  @Nullable Integer getModuleGlVersion();

  @Nullable String getModuleGlVersionIdent();

  @Nullable Integer getFuncGlVersion();

  @Nullable String getFuncGlVersionIdent();

  boolean getIsExport();

  @Nullable String getFeatureRequired();

  @Nullable List<@NotNull String> getParams();

}
