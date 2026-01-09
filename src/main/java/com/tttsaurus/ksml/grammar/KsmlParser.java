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
  // IDENTIFIER | NUMBER | GLSL_SYMBOL
  public static boolean any_chunk(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "any_chunk")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANY_CHUNK, "<any chunk>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, NUMBER);
    if (!r) r = consumeToken(b, GLSL_SYMBOL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // AT EXPORT IDENTIFIER?
  public static boolean export_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "export_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPORT_DECL, null);
    r = consumeTokens(b, 1, AT, EXPORT);
    p = r; // pin = 1
    r = r && export_decl_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FEATURE_DECL, null);
    r = consumeTokens(b, 1, AT, FEATURE, IDENTIFIER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GL_REQUIRES_DECL, null);
    r = consumeTokens(b, 1, AT, GL_REQUIRES, NUMBER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // AT GL_VERSION NUMBER
  public static boolean gl_version_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gl_version_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GL_VERSION_DECL, null);
    r = consumeTokens(b, 1, AT, GL_VERSION, NUMBER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ksml_annotation | any_chunk
  public static boolean item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ITEM, "<item>");
    r = ksml_annotation(b, l + 1);
    if (!r) r = any_chunk(b, l + 1);
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MODULE_DECL, null);
    r = consumeTokens(b, 1, AT, MODULE, IDENTIFIER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // AT REQUIRES IDENTIFIER
  public static boolean requires_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requires_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REQUIRES_DECL, null);
    r = consumeTokens(b, 1, AT, REQUIRES, IDENTIFIER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

}
