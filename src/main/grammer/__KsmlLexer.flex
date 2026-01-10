package com.tttsaurus.ksml.grammar;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.tttsaurus.ksml.grammar.psi.KsmlTypes.*;

%%

%{
  public __KsmlLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class __KsmlLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

IDENTIFIER=[A-Za-z_][A-Za-z_0-9]*
NUMBER=[0-9]+
EOL=\r?\n
WHITE_SPACE=[ \t]+
COMMENT="//"[^\n]*|"/"\*([^*]|\*+[^*/])*\*+"/"

%%
<YYINITIAL> {
  {WHITE_SPACE}       { return WHITE_SPACE; }

  "@"                 { return AT; }
  "module"            { return MODULE; }
  "requires"          { return REQUIRES; }
  "export"            { return EXPORT; }
  "gl_version"        { return GL_VERSION; }
  "gl_requires"       { return GL_REQUIRES; }
  "feature"           { return FEATURE; }

  {IDENTIFIER}        { return IDENTIFIER; }
  {NUMBER}            { return NUMBER; }
  {EOL}               { return EOL; }
  {WHITE_SPACE}       { return WHITE_SPACE; }
  {COMMENT}           { return COMMENT; }

}

[^] { return BAD_CHARACTER; }
