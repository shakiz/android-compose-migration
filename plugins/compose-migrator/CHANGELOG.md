# Changelog

All notable changes to `compose-migrator` are documented here. This project
follows [Semantic Versioning](https://semver.org/).

## [1.0.0] - 2026-06-29

### Added
- Mandatory `@Preview` (light + dark) for every migrated screen, in both the
  `screen` command and the `migration-orchestrator` agent — the artifact used to
  verify visual parity.

### Changed
- `verify` now drives a real visual-parity loop: capture a baseline of the legacy
  XML (screenshot test, or Espresso/UI Automator on an emulator) and diff the
  rendered preview against it, wiring the composable into a screenshot suite where
  available; falls back to the static side-by-side checklist when no emulator/build
  is available, and now reports *how* parity was verified.
- `compose-migration` quality rule #1 reframed: visual parity must be verifiable
  (preview diffed against a baseline) rather than aspirational.

### Docs
- README: added a "How it compares" section positioning the plugin against
  single-skill XML→Compose converters (whole-app program vs. single-screen scope).

## [0.1.0] - 2026-06-29

Initial release.

### Added
- Always-on `compose-migration` guidance skill (bottom-up doctrine, adapt-first,
  four quality rules) plus reference docs: bottom-up strategy, quality rules,
  interop cheatsheet.
- Ten layered slash commands: `inventory`, `design-system`, `architecture`,
  `components`, `screen`, `lists`, `navigation`, `theme`, `verify`, `cleanup`.
- `migration-orchestrator` subagent for end-to-end single-screen migration.
- Optional quality hooks (`kotlin-format`, `check-imports`) that no-op when their
  tooling is unavailable.
- Worked before→after example under `reference/`.
