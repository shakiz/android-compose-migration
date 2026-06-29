# compose-migrator

A Claude Code plugin that migrates Android XML Views to Jetpack Compose **bottom-up**,
adapting to whatever conventions your project already has.

## Why

Generic "convert this XML to Compose" help tends to (a) do too much at once, (b)
re-style your app with Material 3 defaults, and (c) ignore the design system and state
patterns you already use. This plugin encodes the opposite: migrate leaf-first via
interop, **read your project's conventions first and match them**, and hold every step
to four quality rules — visual parity, reuse existing resources, verified imports, and
localization.

## Install

```text
/plugin marketplace add <your-org>/android-compose-migration
/plugin install compose-migrator@android-compose-migration
```

## Use

Run the layered commands in order (each adapts to your codebase):

```text
/compose-migrator:inventory       build a leaf-first migration backlog
/compose-migrator:design-system   theme tokens + reusable component wrappers
/compose-migrator:architecture    state hoisting + ViewModel→UI bridge + host/interop
/compose-migrator:components       migrate leaf widgets
/compose-migrator:screen <name>    migrate one screen end-to-end
/compose-migrator:lists            RecyclerView → LazyColumn/LazyRow
/compose-migrator:navigation       nav graph / Intents → Navigation Compose
/compose-migrator:theme            XML theme → Compose M3 theme
/compose-migrator:verify           imports, parity, build/tests
/compose-migrator:cleanup          delete dead XML / adapters / DataBinding
```

For a one-shot "migrate this whole screen", ask Claude to use the
**`migration-orchestrator`** agent.

The **`compose-migration`** skill is always on — it auto-applies the doctrine whenever
you talk about migrating XML to Compose, even without a command.

## Optional quality hooks

`hooks/hooks.json` wires two `PostToolUse` scripts that run after Claude edits a `.kt`
file:

- **`kotlin-format.sh`** — runs `ktlint -F` or `ktfmt` *if installed*; otherwise no-op.
- **`check-imports.sh`** — warns about common Compose/M3 symbols used without an import.

Both exit cleanly when their tooling is missing, so they never break a project. To
disable, remove the `"hooks"` line from `.claude-plugin/plugin.json` (or delete
`hooks/hooks.json`).

## Reference

`reference/` contains a worked before→after migration of a sample screen, annotated
against the four quality rules — see [`reference/README.md`](reference/README.md).

## The four quality rules

1. **Visual parity** with the legacy XML — not Material 3 defaults.
2. **Reuse existing resources** — your `R.drawable.*`, colors, dimens, components; no
   `material-icons-extended`, no raw `Color()`.
3. **Verify imports** — every new symbol imported (matters when there's no local build).
4. **Localize** — new strings in every `values-*/strings.xml` locale.

See [`skills/compose-migration/`](skills/compose-migration) for the full doctrine.
