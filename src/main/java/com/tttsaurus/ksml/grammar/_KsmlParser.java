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
public class _KsmlParser implements PsiParser, LightPsiParser {

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
  // COMMENT
  public static boolean code_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_comment")) return false;
    if (!nextTokenIs(b, COMMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMENT);
    exit_section_(b, m, CODE_COMMENT, r);
    return r;
  }

  /* ********************************************************** */
  // AT CODE CODE_BLOCK EOL?
  public static boolean code_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, CODE, CODE_BLOCK);
    r = r && code_decl_3(b, l + 1);
    exit_section_(b, m, CODE_DECL, r);
    return r;
  }

  // EOL?
  private static boolean code_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "code_decl_3")) return false;
    consumeToken(b, EOL);
    return true;
  }

  /* ********************************************************** */
  // AT EXPORT IDENTIFIER? EOL
  public static boolean export_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "export_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, EXPORT);
    r = r && export_decl_2(b, l + 1);
    r = r && consumeToken(b, EOL);
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
  // AT FEATURE IDENTIFIER EOL
  public static boolean feature_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, FEATURE, IDENTIFIER, EOL);
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
  // AT GL_REQUIRES NUMBER IDENTIFIER? EOL
  public static boolean gl_requires_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gl_requires_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, GL_REQUIRES, NUMBER);
    r = r && gl_requires_decl_3(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, m, GL_REQUIRES_DECL, r);
    return r;
  }

  // IDENTIFIER?
  private static boolean gl_requires_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gl_requires_decl_3")) return false;
    consumeToken(b, IDENTIFIER);
    return true;
  }

  /* ********************************************************** */
  // AT GL_VERSION NUMBER IDENTIFIER? EOL
  public static boolean gl_version_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gl_version_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, GL_VERSION, NUMBER);
    r = r && gl_version_decl_3(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, m, GL_VERSION_DECL, r);
    return r;
  }

  // IDENTIFIER?
  private static boolean gl_version_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gl_version_decl_3")) return false;
    consumeToken(b, IDENTIFIER);
    return true;
  }

  /* ********************************************************** */
  // ksml_annotation
  //         | code_comment
  //         | WHITE_SPACE
  //         | EOL
  public static boolean item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ITEM, "<item>");
    r = ksml_annotation(b, l + 1);
    if (!r) r = code_comment(b, l + 1);
    if (!r) r = consumeToken(b, WHITE_SPACE);
    if (!r) r = consumeToken(b, EOL);
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
  //                   | code_decl
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
    if (!r) r = code_decl(b, l + 1);
    exit_section_(b, m, KSML_ANNOTATION, r);
    return r;
  }

  /* ********************************************************** */
  // AT MODULE IDENTIFIER EOL
  public static boolean module_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, MODULE, IDENTIFIER, EOL);
    exit_section_(b, m, MODULE_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // AT REQUIRES IDENTIFIER EOL
  public static boolean requires_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requires_decl")) return false;
    if (!nextTokenIs(b, AT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, AT, REQUIRES, IDENTIFIER, EOL);
    exit_section_(b, m, REQUIRES_DECL, r);
    return r;
  }

}
