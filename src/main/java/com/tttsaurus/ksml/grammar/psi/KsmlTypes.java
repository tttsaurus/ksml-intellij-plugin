// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.tttsaurus.ksml.grammar.psi.impl.*;

public interface KsmlTypes {

  IElementType ANY_CHUNK = new IElementType("ANY_CHUNK", null);
  IElementType EXPORT_DECL = new IElementType("EXPORT_DECL", null);
  IElementType FEATURE_DECL = new IElementType("FEATURE_DECL", null);
  IElementType GL_REQUIRES_DECL = new IElementType("GL_REQUIRES_DECL", null);
  IElementType GL_VERSION_DECL = new IElementType("GL_VERSION_DECL", null);
  IElementType ITEM = new IElementType("ITEM", null);
  IElementType KSML_ANNOTATION = new IElementType("KSML_ANNOTATION", null);
  IElementType MODULE_DECL = new IElementType("MODULE_DECL", null);
  IElementType REQUIRES_DECL = new IElementType("REQUIRES_DECL", null);

  IElementType AT = new IElementType("@", null);
  IElementType COMMENT = new IElementType("COMMENT", null);
  IElementType EXPORT = new IElementType("export", null);
  IElementType FEATURE = new IElementType("feature", null);
  IElementType GLSL_SYMBOL = new IElementType("GLSL_SYMBOL", null);
  IElementType GL_REQUIRES = new IElementType("gl_requires", null);
  IElementType GL_VERSION = new IElementType("gl_version", null);
  IElementType IDENTIFIER = new IElementType("IDENTIFIER", null);
  IElementType MODULE = new IElementType("module", null);
  IElementType NUMBER = new IElementType("NUMBER", null);
  IElementType REQUIRES = new IElementType("requires", null);

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ANY_CHUNK) {
        return new AnyChunkImpl(node);
      }
      else if (type == EXPORT_DECL) {
        return new ExportDeclImpl(node);
      }
      else if (type == FEATURE_DECL) {
        return new FeatureDeclImpl(node);
      }
      else if (type == GL_REQUIRES_DECL) {
        return new GlRequiresDeclImpl(node);
      }
      else if (type == GL_VERSION_DECL) {
        return new GlVersionDeclImpl(node);
      }
      else if (type == ITEM) {
        return new ItemImpl(node);
      }
      else if (type == KSML_ANNOTATION) {
        return new KsmlAnnotationImpl(node);
      }
      else if (type == MODULE_DECL) {
        return new ModuleDeclImpl(node);
      }
      else if (type == REQUIRES_DECL) {
        return new RequiresDeclImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
