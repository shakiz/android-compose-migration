# Plan: `compose-migrator` — a Claude Code plugin for XML → Jetpack Compose migration

> Status: **Approved** · Version target: `0.1.0` · Repo: standalone marketplace (this repo)

## Context

We migrated the Barivara app from XML Views to Jetpack Compose using Google's
official `migrate-xml-views-to-jetpack-compose` skill, but that skill missed a lot
of the real-world judgement that made our migration succeed: doing it **bottom-up**
(design system → architecture → components → screens → navigation), insisting on
**visual parity** with the legacy XML, **reusing the project's existing resources**
instead of pulling in Material defaults, **verifying imports** when no local build
is available, and **localizing** every new string.

The goal is to package that accumulated workflow as a **distributable Claude Code
plugin for any Android developer** — not Barivara-specific. It teaches the generic
Google bottom-up migration path, but each step first *reads the target project's own
conventions* (CLAUDE.md, existing design system, existing Compose screens) and
applies them, so it adapts to any codebase. It ships as a **git-based marketplace**
so devs install with `/plugin marketplace add <org>/<repo>` + `/plugin install`.

Outcome: a developer points the plugin at a legacy XML Android app and gets a
guided, layered, reviewable migration with one slash command per phase, an
orchestrator subagent for end-to-end screen migration, quality hooks, and a worked
before→after example.

## Repository layout

This repo **is** the marketplace. It is standalone (not part of the Barivara repo).

```
android-compose-migration/                 # git repo == marketplace
├── .claude-plugin/
│   └── marketplace.json                    # catalog: lists the compose-migrator plugin
├── plugins/
│   └── compose-migrator/
│       ├── .claude-plugin/
│       │   └── plugin.json                  # manifest (name, version, author, paths)
│       ├── skills/
│       │   └── compose-migration/
│       │       ├── SKILL.md                 # always-on guidance (auto-invoked)
│       │       ├── bottom-up-strategy.md    # the phased model + decision tree
│       │       ├── quality-rules.md         # parity / resources / imports / l10n rules
│       │       └── interop-cheatsheet.md    # ComposeView / AndroidView / fragment-compose
│       ├── commands/                        # 10 user-invoked slash commands (one per layer)
│       │   ├── inventory.md
│       │   ├── design-system.md
│       │   ├── architecture.md
│       │   ├── components.md
│       │   ├── screen.md
│       │   ├── lists.md
│       │   ├── navigation.md
│       │   ├── theme.md
│       │   ├── verify.md
│       │   └── cleanup.md
│       ├── agents/
│       │   └── migration-orchestrator.md    # end-to-end "migrate this screen" subagent
│       ├── hooks/
│       │   ├── hooks.json                    # PostToolUse: format + import check (off by default)
│       │   └── scripts/
│       │       ├── kotlin-format.sh
│       │       └── check-imports.sh
│       ├── reference/                        # worked example docs
│       │   ├── README.md
│       │   ├── before/ProfileFragment.xml + ProfileFragment.kt
│       │   └── after/ProfileScreen.kt + notes.md   # annotated before→after
│       ├── README.md
│       ├── CHANGELOG.md
│       └── LICENSE
├── README.md                                # install instructions for devs
└── LICENSE
```

Command namespace follows the plugin name → commands surface as
`/compose-migrator:design-system`, etc. (short form `/design-system` when
unambiguous).

## Manifests

**`plugins/compose-migrator/.claude-plugin/plugin.json`**
```json
{
  "$schema": "https://json.schemastore.org/claude-code-plugin-manifest.json",
  "name": "compose-migrator",
  "displayName": "XML → Jetpack Compose Migrator",
  "description": "Bottom-up, project-adaptive workflow to migrate Android XML Views to Jetpack Compose, with per-layer commands, an orchestrator agent, and quality gates.",
  "version": "0.1.0",
  "author": { "name": "Sakhawat Hossain" },
  "license": "MIT",
  "keywords": ["android", "jetpack", "compose", "migration", "xml-views", "kotlin"],
  "hooks": "./hooks/hooks.json"
}
```

> Note: `skills/`, `commands/`, and `agents/` are conventional directories that the
> plugin loader auto-discovers, so they are **not** listed in the manifest (the
> `agents`/`skills` manifest fields are for *additional* paths and expect a `.md`
> file / `./`-prefixed dir, not the default directory — listing them fails
> `claude plugin validate`). Only `hooks` (a file path) is declared explicitly.

**`.claude-plugin/marketplace.json`** (repo root) lists one plugin with a relative
`source: "./plugins/compose-migrator"`.

## The always-on guidance skill (`skills/compose-migration/SKILL.md`)

Frontmatter: `name`, a `description` written to auto-trigger on migration intent
(e.g. "migrate … XML … to Compose", "convert this layout/fragment/activity to
Compose"), and `allowed-tools: Read Grep Glob`.

Body teaches the **core doctrine** (generic, not Barivara):
- Gradual, incremental, **bottom-up** — never big-bang. Layer order = the command order below.
- **Adapt first, generate second**: before writing any Compose, read the project's
  `CLAUDE.md`, any `designsystem/` package, and 1–2 already-migrated screens to learn
  local naming, theme tokens, and the ViewModel→UI bridge. Match them.
- The four quality rules (detailed in `quality-rules.md`): **visual parity** with the
  legacy XML; **reuse the project's existing resources** (its own `R.drawable.*`,
  colors, dimens — avoid `material-icons-extended` / raw `Color()` / magic dp);
  **verify every new symbol has an import**; **localize** new strings to every
  `values-*/strings.xml` the project ships.
- Points to the per-layer commands as the execution surface and to the orchestrator
  agent for one-shot screen migration.

The three companion `.md` files are reference material the skill and commands link to
(`bottom-up-strategy.md`, `quality-rules.md`, `interop-cheatsheet.md`).

## The 10 layered commands (`commands/*.md`)

Each is a slash-command markdown file (frontmatter: `description`, `argument-hint`
where relevant, `allowed-tools`). Each follows the same shape: **(1) read the
project's conventions for this layer, (2) apply the generic bottom-up guidance,
(3) emit/modify code, (4) print a checklist + what to run next.** Commands use the
``!`backtick` `` dynamic-injection trick to pre-load live project context (e.g. list
the XML files, grep the theme).

| Command | Purpose / key content |
|---|---|
| `/inventory` | Scan `res/layout/`, custom Views, themes, nav graph, RecyclerViews, DataBinding usage → produce a prioritized **migration backlog** (leaf-first). No edits. |
| `/design-system` | Establish the Compose foundation: detect or scaffold theme (colors, typography, shape, spacing tokens) + a set of semantic wrapper components; map the project's XML styles/`themes.xml` to Compose tokens. |
| `/architecture` | Set up state hoisting + the ViewModel→UI bridge (StateFlow `collectAsStateWithLifecycle`, or LiveData `observeAsState` if that's what the project uses), `BaseComposeActivity`/host pattern, and the interop boundary (ComposeView/AndroidView/fragment-compose). |
| `/components` | Migrate **leaf widgets** (buttons, cards, list rows, dialogs) into reusable composables, parity-matched to the XML. |
| `/screen <name>` | Migrate **one screen** end-to-end into a stateless `@Composable *Screen(state, callbacks)` + host wiring; the workhorse command. |
| `/lists` | RecyclerView/Adapter/ViewHolder → `LazyColumn`/`LazyRow` with `items(...)`. |
| `/navigation` | XML nav graph / Activity-Intent flow → Navigation Compose (type-safe routes, arg passing, result hand-off, back-stack reset). |
| `/theme` | XML theme → Compose M3 theme; optional M2→M3 alignment; keep XML + Compose visually aligned during the mixed period. |
| `/verify` | Static verification appropriate when no local build is wired: confirm imports resolve, parity checklist, run the project's build/test command if available (`./gradlew assembleDebug`, `lint`, `test`). |
| `/cleanup` | After a surface is fully Compose: delete dead XML layouts, adapters, DataBinding/`@Bindable`, and orphaned drawables; drop now-unused deps. |

All command text is **generic**; Barivara names appear only inside the `reference/`
worked example, never hard-coded into command logic.

## Orchestrator subagent (`agents/migration-orchestrator.md`)

Frontmatter: `name: migration-orchestrator`, `description` ("migrate an entire screen
end-to-end"), `tools: Read Write Edit Bash Grep Glob`, `model: opus`, `effort: high`.
Body: run the layers in order for a single target — read conventions → ensure
design-system/architecture prerequisites exist → migrate the screen + its leaf
components + its list → wire navigation → verify imports & parity → summarize
remaining manual steps. It defers to the same `quality-rules.md`. Good for "just
migrate `ProfileFragment` for me" one-shot use.

## Quality hooks (`hooks/hooks.json`, off by default)

`PostToolUse` matching `Write|Edit` on `*.kt` runs two scripts via
`${CLAUDE_PLUGIN_ROOT}/hooks/scripts/`:
- `kotlin-format.sh` — runs `ktlint -F` / `ktfmt` **only if present on PATH**, else no-ops cleanly.
- `check-imports.sh` — greps the edited file for common Compose/M3 symbols used without a matching `import` and warns (addresses the "missing import not caught without a local build" failure mode).

Both are guarded to exit 0 when their tooling is absent so the plugin never breaks a
project that lacks the tools. README documents how to enable.

## Worked example (`reference/`)

A real, self-contained before→after for one screen (a generic `ProfileFragment` + its
XML), plus `notes.md` annotating each decision against the four quality rules.
Distilled from the Barivara migration but **renamed/genericized** so no proprietary
detail ships. Lets devs see the conventions applied, not just described.

## Distribution & rollout

1. Commit this repo structure, push to a GitHub repo the user owns (e.g.
   `<org>/android-compose-migration`).
2. Devs run:
   ```
   /plugin marketplace add <org>/android-compose-migration
   /plugin install compose-migrator@android-compose-migration
   ```
3. README documents the per-layer workflow and the optional hooks.
4. Versioning: explicit semver in `plugin.json` (start `0.1.0`); bump to publish updates.

## Verification

- `claude plugin validate ./plugins/compose-migrator` and `claude plugin validate .`
  (marketplace) must pass.
- Local install test: `/plugin marketplace add ./android-compose-migration` →
  `/plugin install compose-migrator@...` → `/reload-plugins`.
- Smoke-test each surface against the Barivara repo itself (a known XML→Compose
  codebase): `/compose-migrator:inventory` should produce a sane backlog;
  `/compose-migrator:screen <name>` and the orchestrator agent should adapt to
  Barivara's existing design system rather than inventing new components.
- Confirm hooks no-op gracefully on a machine without ktlint.

## Open items (minor, can decide at build time)

1. **Plugin name `compose-migrator` vs `compose`** — the latter yields the shorter
   `/compose:design-system` prefix but is a more generic name. Default: `compose-migrator`.
2. **GitHub repo name / owner** for the marketplace — default `android-compose-migration`.
3. Whether to also publish to a public marketplace listing later (out of scope for v0.1).
