<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# ksml-intellij-plugin Changelog

## [0.3.2]
### Added
- Add checks for declaration order, function annotations, and `@code` blocks
- Validate import names, duplicate imports, and unresolved imports
- Add `Remove declaration` quick fixes for duplicate imports
- Add GLSL version and profile completion
- Add module completion for `@requires` and `// @import`
- Add navigation from `@requires` to module declarations

### Changed
- Align GLSL version requirements and `@gl_requires` overrides with KSML spec v2.1
- Remove the default GLSL profile setting and profile inference
- Display version requirements as `GL330 CORE`, `GL330 COM`, or `GL140`
- Report module requirement mismatches as notices and function requirement mismatches as warnings

### Fixed
- Fix internal function visibility checks across overloads
- Fix ordinary comments being treated as imports
- Fix import completion and diagnostic ranges in injected GLSL comments
- Fix release draft generation for manually maintained changelogs

## [0.3.1]
### Fixed
- Fix delayed editor refresh and reduce latency when updating KSML annotations and import badges
- Fix import badges not appearing in already open GLSL files
- Fix KSML stub storage and function indexing
- Fix GLSL version and profile detection in module compatibility checks

### Improved
- Rework editor refresh scheduling
- Optimize function call analysis and reduce redundant metadata parsing
- Improve badge rendering with antialiasing
- Improve GitHub Release changelog generation

## [0.3.0]
### Added
- Project settings for the default GLSL profile and standard library module source URLs
- IDE settings to discover and install releases of GLSL plugin hotfix

### Changed
- Display GLSL import requirements as `GLXX0C` or `GLXX0`, without the `core` suffix
- Add a plugin icon

### Fixed
- Fix GlslModuleUsageAnnotator warning struct usage

## [0.2.0]
### Added
- Real-time function call precondition analysis
- Code completion
- Go to declaration
- Find usages (usage UI inlay support)
- Rename function declaration and all occurrences

## [0.1.0]
### Added
- KSML syntax & highlight
- KSML in GLSL `@import` highlight and reference `CTRL Click` resolver
- Special renderers for `@import`: displays the GL version requirement besides `@import`
- Delegate GLSL code blocks inside KSML to the GLSL plugin via `LanguageInjector`
