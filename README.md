# Android XML → Jetpack Compose Migration (Claude Code marketplace)

[![Release](https://img.shields.io/github/v/release/shakiz/android-compose-migration?sort=semver)](https://github.com/shakiz/android-compose-migration/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A Claude Code **plugin marketplace** containing [`compose-migrator`](plugins/compose-migrator) —
a bottom-up, project-adaptive workflow for migrating Android XML Views to Jetpack
Compose.

It teaches the generic, incremental Compose migration path (the one Google
recommends), but every step first **reads your project's own conventions** — your
`CLAUDE.md`, your design-system package, your already-migrated screens — and matches
them, instead of imposing a fixed style. Bring any legacy XML Android app.

## Install

```text
/plugin marketplace add shakiz/android-compose-migration
/plugin install compose-migrator@android-compose-migration
```

Pin to a released version for reproducible installs:

```text
/plugin marketplace add shakiz/android-compose-migration@v0.1.0
```

Then run `/compose-migrator:inventory` in your Android project to get started.

> For local development against a checkout: `/plugin marketplace add ./android-compose-migration`.

## Requirements

- **Claude Code** with plugin support.
- An **Android project** using XML Views (any state/navigation stack — the workflow adapts).
- Optional: `ktlint` or `ktfmt` on your `PATH` to enable the formatting hook (it no-ops if absent).

## What's inside

| Surface | What it does |
|---|---|
| **Guidance skill** | Always-on doctrine: bottom-up, adapt-first, four quality rules. Auto-triggers when you talk about migrating XML to Compose. |
| **10 slash commands** | One per migration layer — see below. |
| **Orchestrator agent** | `migration-orchestrator`: migrate one whole screen end-to-end. |
| **Quality hooks** | Optional post-edit format + import check (off by default; no-op without tooling). |
| **Worked example** | A real before→after migration with annotated decisions. |

## How it works

Two principles drive every command:

- **Adapt first, generate second.** Before writing any Compose, the workflow reads your
  `CLAUDE.md`, your design-system package, an already-migrated screen, and your
  `res/values*/` resources — then matches them. An existing convention always wins;
  defaults are only scaffolded when you have none yet.
- **Bottom-up, never big-bang.** Migrate leaf-first via View↔Compose interop so the app
  stays shippable at every step — inventory → design system → architecture → components
  → screens → lists → navigation → theme → cleanup.

Every step is held to **four quality rules**:

1. **Visual parity** with the legacy XML — not Material 3 defaults.
2. **Reuse existing resources** — your `R.drawable.*`, colors, dimens, components.
3. **Verify imports** — every new symbol imported (matters when there's no local build).
4. **Localize** — new strings added to every `values-*/strings.xml` locale.

### The bottom-up command flow

```text
/compose-migrator:inventory       scan XML, build a leaf-first migration backlog
/compose-migrator:design-system   theme + colors + typography + reusable components
/compose-migrator:architecture    state hoisting, ViewModel→UI bridge, interop, host
/compose-migrator:components       migrate leaf widgets (buttons, cards, rows)
/compose-migrator:screen <name>    migrate one screen end-to-end
/compose-migrator:lists            RecyclerView/Adapter → LazyColumn/LazyRow
/compose-migrator:navigation       XML nav graph / Intents → Navigation Compose
/compose-migrator:theme            XML theme → Compose Material 3 theme
/compose-migrator:verify           build, check imports + visual parity
/compose-migrator:cleanup          delete dead XML / adapters / DataBinding
```

(Short forms like `/inventory` work when the name is unambiguous.)

### A typical session

```text
/compose-migrator:inventory          # see the backlog, pick a leaf-first order
/compose-migrator:design-system      # establish/confirm theme tokens + wrappers
/compose-migrator:screen ProfileScreen
/compose-migrator:verify             # imports + parity before moving on
```

Prefer a hands-off pass? Ask Claude to use the **`migration-orchestrator`** agent to take
a single screen end-to-end. The **`compose-migration`** skill applies the doctrine
automatically even without an explicit command.

## Learn more

- Plugin details, hooks, and the worked example: [`plugins/compose-migrator/README.md`](plugins/compose-migrator/README.md)
- Full migration doctrine: [`plugins/compose-migrator/skills/compose-migration/`](plugins/compose-migrator/skills/compose-migration)
- Design notes: [`docs/PLAN.md`](docs/PLAN.md)
- Changelog: [`plugins/compose-migrator/CHANGELOG.md`](plugins/compose-migrator/CHANGELOG.md)

## License

MIT — see [LICENSE](LICENSE).
