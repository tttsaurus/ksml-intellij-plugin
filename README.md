# Kirino Shader Meta Language Support

## Dev Env / Build

- Import the build script and download deps
- Install plugin `Grammar-Kit` by JetBrains
- Install plugin `Plugin DevKit` by JetBrains

**Dev Tips**
- `./gradlew runIde` to start a sandbox IDE instance
- Install plugin `GLSL` by Roi Mordechay in your sandbox IDE
- `./gradlew build` (saves jars to `build/libs`)

**Update Parser & Lexer**:
- Modify `src/main/grammar/ksml.bnf` to update BNF
- Update parser code `src/main/java/...`:<br>
  Right Click `ksml.bnf` ⟶ Open Menu ⟶ `Generate Parser Code`
- Update lexer code `src/main/java/...`:<br>
  Right Click `ksml.bnf` ⟶ Open Menu ⟶ `Generate JFlex Lexer` ⟶ Save to `src/main/grammar/__KsmlLexer.flex`
  ⟶ Right Click `__KsmlLexer.flex` ⟶ Open Menu ⟶ `Run JFlex Generator`



<!-- Plugin description -->
This is the IntelliJ support of Kirino Shader Meta Language for GLSL.
<!-- Plugin description end -->
