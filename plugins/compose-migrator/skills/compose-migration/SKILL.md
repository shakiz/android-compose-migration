---
name: compose-migration
description: >-
  Use when migrating Android XML Views to Jetpack Compose — converting a layout,
  Fragment, Activity, View, RecyclerView, or whole screen from the legacy View
  system to Compose, or planning such a migration. Provides the bottom-up,
  project-adaptive doctrine and the quality rules every migration step must follow.
allowed-tools: Read, Grep, Glob
---

# XML → Jetpack Compose migration doctrine

This skill governs **how** to migrate any Android app from XML Views to Jetpack
Compose. It is intentionally generic: it does not assume a specific design system,
state-management library, or navigation setup. Instead it tells you to **learn the
target project's conventions first, then apply them**.

When the user wants to migrate a screen/layout/fragment, or asks for a migration
plan, follow this doctrine and route execution through the per-layer slash commands
(`/compose-migrator:inventory`, `…:design-system`, `…:architecture`, `…:components`,
`…:screen`, `…:lists`, `…:navigation`, `…:theme`, `…:verify`, `…:cleanup`). For a
one-shot "migrate this whole screen for me", hand off to the `migration-orchestrator`
subagent.

## 1. Bottom-up, never big-bang

Migrate incrementally, leaf-first, using interop so the app stays shippable the whole
time. The layer order is:

1. **Inventory** — know what exists before touching anything.
2. **Design system** — theme tokens + reusable component wrappers (the foundation
   everything else builds on).
3. **Architecture** — state hoisting, the ViewModel→UI bridge, the Compose host, and
   the View↔Compose interop boundary.
4. **Leaf components** — buttons, cards, list rows, dialogs.
5. **Screens** — assemble components into stateless `*Screen` composables.
6. **Lists** — RecyclerView/Adapter → `LazyColumn`/`LazyRow`.
7. **Navigation** — XML nav graph / Intent flows → Navigation Compose.
8. **Theme** — finish the XML-theme → Compose-theme story; align M2→M3 if needed.
9. **Cleanup** — delete dead XML, adapters, DataBinding once a surface is fully
   Compose.

See `bottom-up-strategy.md` for the decision tree and the interop options.

## 2. Adapt first, generate second

**Before writing any Compose code**, read the project to learn its conventions and
match them — do not invent a new style:

- `CLAUDE.md` / `README` / `docs/` for stated architecture and naming rules.
- Any existing design-system package (search for `*Theme`, `*Button`, `*TextField`,
  a `designsystem`/`ui/theme`/`ui/components` package) — reuse those components.
- One or two already-migrated Compose screens — copy their `*Screen(...)` signature,
  their ViewModel→UI bridge (StateFlow + `collectAsStateWithLifecycle`, or LiveData +
  `observeAsState`), and how they handle navigation callbacks and dialogs.
- `res/values*/` for the existing color/dimen/string resources and `themes.xml`.

If the project has none of these yet, `/compose-migrator:design-system` and
`…:architecture` scaffold sensible, idiomatic defaults — but an existing convention
always wins.

## 3. The four quality rules

Every migration step must satisfy all four (full detail in `quality-rules.md`):

1. **Visual parity** — the Compose output must look like the XML it replaces (size,
   spacing, color, corner radius, icon treatment). Defer cosmetic "improvements" to a
   separate follow-up. Don't silently re-style the app with Material 3 defaults.
2. **Reuse the project's existing resources** — use its own `R.drawable.*` icons,
   color/dimen resources, and design-system components. Avoid pulling in
   `material-icons-extended`, raw `Color(0xFF…)`, or magic `.dp` literals when a
   token or resource already exists.
3. **Verify imports** — every newly used symbol (Compose APIs, M3 components,
   modifiers) must have its `import`. In projects where you can't run a local build,
   a missing import is only caught much later — confirm imports before declaring done.
4. **Localize** — add every new user-visible string to *all* `values-*/strings.xml`
   locales the project ships (e.g. `values/` and `values-bn/`), not just the default.

## 4. State and lifecycle

Hoist state out of composables: screens are stateless and take `state` + callbacks;
the ViewModel owns the state. Collect with lifecycle awareness
(`collectAsStateWithLifecycle()` for `StateFlow`, `observeAsState()` for `LiveData`)
— match whichever the project already uses rather than rewriting the ViewModel layer
mid-migration. Keep transient UI-only state (search text, dialog visibility) local
via `remember`.

## 5. Output discipline

Match the surrounding code's comment density — prefer no comment when the code is
self-explanatory, one short line otherwise. Never leave a half-migrated file that
won't compile.
