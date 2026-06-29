---
description: Migrate leaf widgets (buttons, cards, list rows, dialogs) from XML into reusable, parity-matched composables.
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
argument-hint: "[widget or layout file, optional]"
---

# Compose migration — Leaf components

Convert the small, reusable widgets first. Focus: `$ARGUMENTS` (if empty, migrate the
highest-reuse leaf widgets from the inventory backlog).

## What to do

1. **Pick a leaf** — a button style, a card, a list row item, a dialog, a search bar:
   something self-contained that several screens reuse.
2. **Read the legacy XML** it replaces (the `res/layout` item layout and any
   `res/drawable` background). Record height, padding, corner radius, fill, border,
   text style, icon source/tint — this is the parity contract.
3. **Build it on the design system.** Compose the new widget from the design-system
   wrappers and theme tokens created by `/compose-migrator:design-system`. Don't drop
   raw M3 widgets into it. Render icons with `painterResource(R.drawable.ic_*)`.
4. **Keep it stateless and reusable** — inputs in, callbacks out, no ViewModel
   reference. Hoist any state to the caller.
5. **Add a `@Preview`** (light + dark) populated with realistic sample data.

## Quality gates

- Visual parity with the item XML.
- Reuse existing icons/colors/dimens — no `material-icons-extended`, no raw `Color()`.
- Verify imports.
- Any user-visible text via `stringResource(...)`, localized to all locales.

## Output

List the components migrated (file paths) and which screens can now reuse them. When
the leaf widgets a screen needs are ready, suggest `/compose-migrator:screen <name>`.
