<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# ksml-intellij-plugin Changelog

## [Unreleased]

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

[Unreleased]: https://github.com/tttsaurus/ksml-intellij-plugin/compare/v0.3.1...HEAD
[0.3.1]: https://github.com/tttsaurus/ksml-intellij-plugin/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/tttsaurus/ksml-intellij-plugin/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/tttsaurus/ksml-intellij-plugin/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/tttsaurus/ksml-intellij-plugin/commits/v0.1.0
