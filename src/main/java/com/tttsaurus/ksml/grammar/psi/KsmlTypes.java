// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.tttsaurus.ksml.KsmlElementType;
import com.tttsaurus.ksml.KsmlTokenType;
import com.tttsaurus.ksml.grammar.psi.impl.*;

public interface KsmlTypes {

  IElementType CODE_COMMENT = new KsmlElementType("CODE_COMMENT");
  IElementType EXPORT_DECL = new KsmlElementType("EXPORT_DECL");
  IElementType FEATURE_DECL = new KsmlElementType("FEATURE_DECL");
  IElementType GL_REQUIRES_DECL = new KsmlElementType("GL_REQUIRES_DECL");
  IElementType GL_VERSION_DECL = new KsmlElementType("GL_VERSION_DECL");
  IElementType ITEM = new KsmlElementType("ITEM");
  IElementType KSML_ANNOTATION = new KsmlElementType("KSML_ANNOTATION");
  IElementType MODULE_DECL = new KsmlElementType("MODULE_DECL");
  IElementType REQUIRES_DECL = new KsmlElementType("REQUIRES_DECL");

  IElementType AT = new KsmlTokenType("@");
  IElementType COMMENT = new KsmlTokenType("COMMENT");
  IElementType EOL = new KsmlTokenType("EOL");
  IElementType EXPORT = new KsmlTokenType("export");
  IElementType FEATURE = new KsmlTokenType("feature");
  IElementType GL_REQUIRES = new KsmlTokenType("gl_requires");
  IElementType GL_VERSION = new KsmlTokenType("gl_version");
  IElementType IDENTIFIER = new KsmlTokenType("IDENTIFIER");
  IElementType MODULE = new KsmlTokenType("module");
  IElementType NUMBER = new KsmlTokenType("NUMBER");
  IElementType REQUIRES = new KsmlTokenType("requires");
  IElementType WHITE_SPACE = new KsmlTokenType("WHITE_SPACE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == CODE_COMMENT) {
        return new KsmlCodeCommentImpl(node);
      }
      else if (type == EXPORT_DECL) {
        return new KsmlExportDeclImpl(node);
      }
      else if (type == FEATURE_DECL) {
        return new KsmlFeatureDeclImpl(node);
      }
      else if (type == GL_REQUIRES_DECL) {
        return new KsmlGlRequiresDeclImpl(node);
      }
      else if (type == GL_VERSION_DECL) {
        return new KsmlGlVersionDeclImpl(node);
      }
      else if (type == ITEM) {
        return new KsmlItemImpl(node);
      }
      else if (type == KSML_ANNOTATION) {
        return new KsmlKsmlAnnotationImpl(node);
      }
      else if (type == MODULE_DECL) {
        return new KsmlModuleDeclImpl(node);
      }
      else if (type == REQUIRES_DECL) {
        return new KsmlRequiresDeclImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
