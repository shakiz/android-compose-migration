# Changelog

All notable changes to `compose-migrator` are documented here. This project
follows [Semantic Versioning](https://semver.org/).

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
