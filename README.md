# Android XML → Jetpack Compose Migration (Claude Code marketplace)

A Claude Code **plugin marketplace** containing [`compose-migrator`](plugins/compose-migrator) —
a bottom-up, project-adaptive workflow for migrating Android XML Views to Jetpack
Compose.

It teaches the generic, incremental Compose migration path (the one Google
recommends), but every step first **reads your project's own conventions** — your
`CLAUDE.md`, your design-system package, your already-migrated screens — and matches
them, instead of imposing a fixed style. Bring any legacy XML Android app.

## Install

```text
/plugin marketplace add <your-org>/android-compose-migration
/plugin install compose-migrator@android-compose-migration
```

Then run `/compose-migrator:inventory` in your Android project to get started.

> Replace `<your-org>` with wherever you push this repo. For local development:
> `/plugin marketplace add ./android-compose-migration`.

## What's inside

| Surface | What it does |
|---|---|
| **Guidance skill** | Always-on doctrine: bottom-up, adapt-first, four quality rules. Auto-triggers when you talk about migrating XML to Compose. |
| **10 slash commands** | One per migration layer — see below. |
| **Orchestrator agent** | `migration-orchestrator`: migrate one whole screen end-to-end. |
| **Quality hooks** | Optional post-edit format + import check (off by default; no-op without tooling). |
| **Worked example** | A real before→after migration with annotated decisions. |

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

## License

MIT — see [LICENSE](LICENSE).
