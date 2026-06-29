---
description: Establish or extend the Compose design-system foundation — theme tokens and reusable component wrappers — mapped from the project's existing XML styles.
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
argument-hint: "[component or token to focus on, optional]"
---

# Compose migration — Design system

Build the Compose **foundation** every screen will sit on: theme tokens (color,
typography, shape, spacing) plus a set of semantic component wrappers. Focus:
`$ARGUMENTS` (if empty, set up / audit the whole foundation).

## Existing design assets

Themes & styles:

!`find . -name "themes.xml" -o -name "styles.xml" -o -name "colors.xml" -o -name "dimens.xml" 2>/dev/null | grep -v build`

Existing Compose theme / components:

!`find . -path "*designsystem*" -o -path "*ui/theme*" -o -path "*ui/components*" 2>/dev/null | grep -v build | head -60`

## What to do

1. **Detect, don't duplicate.** If a design-system / theme package already exists,
   read it and *extend* it. Only scaffold from scratch when there is none.
2. **Map XML → tokens.** Translate `colors.xml`, `dimens.xml`, text appearances, and
   shape drawables into Compose tokens:
   - `ColorScheme` (light + dark) from the brand palette — keep brand colors, don't
     adopt M3 defaults that change the look.
   - `Typography` from the existing text appearances.
   - `Shapes` from corner radii used in shape drawables.
   - A spacing scale from the recurring `dimens` values.
   Wrap them in an `AppTheme { }` composable (`MaterialTheme` + any custom
   `CompositionLocal`s the project wants for non-M3 tokens).
3. **Create semantic component wrappers** matched to the legacy look — typically a
   button (with the project's variants), a text field (parity height/shape/fill), a
   top bar, a card, a dialog, an icon wrapper that takes `painterResource(...)`. These
   wrap M3 but lock in the brand spec so screens never touch raw M3 directly.
4. **Add a `@Preview`** (light + dark, and a large-font variant) for each component.

## Quality gates (see the skill's `quality-rules.md`)

- Visual parity with the XML — verify against the real `themes.xml`/drawables.
- No raw `Color(0xFF…)` / magic `.dp` outside the theme package.
- Icon wrapper uses `painterResource(R.drawable.ic_*)`, not `Icons.Filled.*`.
- Verify imports.

## Output

List the tokens and components created/extended (with file paths), then suggest
`/compose-migrator:architecture` next.
