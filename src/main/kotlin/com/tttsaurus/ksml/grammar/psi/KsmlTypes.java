// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.tttsaurus.ksml.grammar.psi.impl.*;

public interface KsmlTypes {

  IElementType EXPORT_DECL = new IElementType("EXPORT_DECL", null);
  IElementType FEATURE_DECL = new IElementType("FEATURE_DECL", null);
  IElementType GLSL_CHUNK = new IElementType("GLSL_CHUNK", null);
  IElementType GLSL_FUNCTION = new IElementType("GLSL_FUNCTION", null);
  IElementType GLSL_STRUCT = new IElementType("GLSL_STRUCT", null);
  IElementType GLSL_TOKEN = new IElementType("GLSL_TOKEN", null);
  IElementType GL_REQUIRES_DECL = new IElementType("GL_REQUIRES_DECL", null);
  IElementType GL_VERSION_DECL = new IElementType("GL_VERSION_DECL", null);
  IElementType ITEM = new IElementType("ITEM", null);
  IElementType KSML_ANNOTATION = new IElementType("KSML_ANNOTATION", null);
  IElementType MODULE_DECL = new IElementType("MODULE_DECL", null);
  IElementType REQUIRES_DECL = new IElementType("REQUIRES_DECL", null);

  IElementType ANY = new KsmlTokenType("ANY");
  IElementType AT = new KsmlTokenType("@");
  IElementType COMMENT = new KsmlTokenType("COMMENT");
  IElementType EXPORT = new KsmlTokenType("export");
  IElementType FEATURE = new KsmlTokenType("feature");
  IElementType GLSL_FUNCTION_HEAD = new KsmlTokenType("GLSL_FUNCTION_HEAD");
  IElementType GLSL_STRUCT_HEAD = new KsmlTokenType("GLSL_STRUCT_HEAD");
  IElementType GL_REQUIRES = new KsmlTokenType("gl_requires");
  IElementType GL_VERSION = new KsmlTokenType("gl_version");
  IElementType IDENTIFIER = new KsmlTokenType("IDENTIFIER");
  IElementType MODULE = new KsmlTokenType("module");
  IElementType NUMBER = new KsmlTokenType("NUMBER");
  IElementType REQUIRES = new KsmlTokenType("requires");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == EXPORT_DECL) {
        return new ExportDeclImpl(node);
      }
      else if (type == FEATURE_DECL) {
        return new FeatureDeclImpl(node);
      }
      else if (type == GLSL_CHUNK) {
        return new GlslChunkImpl(node);
      }
      else if (type == GLSL_FUNCTION) {
        return new GlslFunctionImpl(node);
      }
      else if (type == GLSL_STRUCT) {
        return new GlslStructImpl(node);
      }
      else if (type == GLSL_TOKEN) {
        return new GlslTokenImpl(node);
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
