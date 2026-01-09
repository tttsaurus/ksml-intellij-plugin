// This is a generated file. Not intended for manual editing.
package com.tttsaurus.ksml.grammar;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.tttsaurus.ksml.grammar.psi.KsmlTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class KsmlParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return file(b, l + 1);
  }

  /* ********************************************************** */
  // AT EXPORT IDENTIFIER?
  public static boolean export_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "export_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, EXPORT);
    r = r && export_decl_2(b, l + 1);
    exit_section_(b, m, EXPORT_DECL, r);
    return r;
  }

  // IDENTIFIER?
  private static boolean export_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "export_decl_2")) return false;
    consumeToken(b, IDENTIFIER);
    return true;
  }

  /* ********************************************************** */
  // AT FEATURE IDENTIFIER
  public static boolean feature_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, FEATURE, IDENTIFIER);
    exit_section_(b, m, FEATURE_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // item*
  static boolean file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "file", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // AT GL_REQUIRES NUMBER
  public static boolean gl_requires_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gl_requires_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, GL_REQUIRES, NUMBER);
    exit_section_(b, m, GL_REQUIRES_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // AT GL_VERSION NUMBER
  public static boolean gl_version_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gl_version_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, GL_VERSION, NUMBER);
    exit_section_(b, m, GL_VERSION_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // glsl_token+
  public static boolean glsl_chunk(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glsl_chunk")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GLSL_CHUNK, "<glsl chunk>");
    r = glsl_token(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!glsl_token(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "glsl_chunk", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // GLSL_FUNCTION_HEAD glsl_chunk
  public static boolean glsl_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glsl_function")) return false;
    if (!nextTokenIs(b, GLSL_FUNCTION_HEAD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GLSL_FUNCTION_HEAD);
    r = r && glsl_chunk(b, l + 1);
    exit_section_(b, m, GLSL_FUNCTION, r);
    return r;
  }

  /* ********************************************************** */
  // GLSL_STRUCT_HEAD glsl_chunk
  public static boolean glsl_struct(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glsl_struct")) return false;
    if (!nextTokenIs(b, GLSL_STRUCT_HEAD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GLSL_STRUCT_HEAD);
    r = r && glsl_chunk(b, l + 1);
    exit_section_(b, m, GLSL_STRUCT, r);
    return r;
  }

  /* ********************************************************** */
  // NUMBER
  //             | IDENTIFIER
  //             | ANY
  public static boolean glsl_token(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glsl_token")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GLSL_TOKEN, "<glsl token>");
    r = consumeToken(b, NUMBER);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, ANY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ksml_annotation
  //         | glsl_function
  //         | glsl_struct
  //         | glsl_chunk
  public static boolean item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ITEM, "<item>");
    r = ksml_annotation(b, l + 1);
    if (!r) r = glsl_function(b, l + 1);
    if (!r) r = glsl_struct(b, l + 1);
    if (!r) r = glsl_chunk(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // module_decl
  //                   | requires_decl
  //                   | gl_version_decl
  //                   | gl_requires_decl
  //                   | export_decl
  //                   | feature_decl
  public static boolean ksml_annotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ksml_annotation")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_decl(b, l + 1);
    if (!r) r = requires_decl(b, l + 1);
    if (!r) r = gl_version_decl(b, l + 1);
    if (!r) r = gl_requires_decl(b, l + 1);
    if (!r) r = export_decl(b, l + 1);
    if (!r) r = feature_decl(b, l + 1);
    exit_section_(b, m, KSML_ANNOTATION, r);
    return r;
  }

  /* ********************************************************** */
  // AT MODULE IDENTIFIER
  public static boolean module_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, MODULE, IDENTIFIER);
    exit_section_(b, m, MODULE_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // AT REQUIRES IDENTIFIER
  public static boolean requires_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requires_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, REQUIRES, IDENTIFIER);
    exit_section_(b, m, REQUIRES_DECL, r);
    return r;
  }

}
