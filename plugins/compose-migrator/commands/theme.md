---
description: Migrate the XML theme to a Compose Material 3 theme, optionally aligning Material 2 → Material 3, keeping both systems visually consistent.
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
---

# Compose migration — Theme

Finish the theming story: a Compose M3 theme that reproduces the app's look, kept
aligned with the XML theme during the mixed period.

## Current theming

!`find . -path "*/build/*" -prune -o \( -name "themes.xml" -o -name "colors.xml" \) -print 2>/dev/null`
!`grep -rhoE "Theme\\.MaterialComponents|Theme\\.Material3|parent=\"[^\"]*\"" $(find . -path "*/build/*" -prune -o -name themes.xml -print 2>/dev/null) 2>/dev/null | head`

## What to do

1. **Read the XML theme(s)** including the `night`/`-v` variants. Note the parent
   (MaterialComponents vs Material3), the color attributes, text appearances, and
   shape appearances.
2. **Build the Compose theme** (if not already done in `/compose-migrator:design-system`):
   - `lightColorScheme` / `darkColorScheme` mapping the XML colors to M3 roles. Keep
     the brand colors — map a legacy value to the nearest semantic slot rather than
     adopting an M3 default that changes the look.
   - `Typography` from the text appearances.
   - `Shapes` from the shape appearances.
   - An `AppTheme { }` that also sets the system bar colors / status bar to match.
3. **Material 2 → Material 3** (if the XML theme is still MaterialComponents/M2): map
   `colorPrimary`/`colorSecondary`/surface/background to the M3 `ColorScheme` roles;
   be explicit about `surfaceVariant`, `outline`, and container colors that have no M2
   equivalent.
4. **Keep both systems aligned** — while XML screens remain, ensure the XML theme and
   the Compose theme read the same brand values so screens look identical regardless of
   which system rendered them.

## Quality gates

- Visual parity in light **and** dark.
- No raw `Color(0xFF…)` in screens — only in the theme/token definitions.
- Verify imports.

## Output

The Compose theme files, the M2→M3 mapping (if done), and any remaining theme-drift
risk between XML and Compose.
