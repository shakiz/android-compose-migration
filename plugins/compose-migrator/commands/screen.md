---
description: Migrate one screen end-to-end from an XML layout into a stateless @Composable *Screen plus its host wiring.
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
argument-hint: "<screen or layout name>"
---

# Compose migration — Screen

Migrate the screen **`$ARGUMENTS`** end-to-end. This is the workhorse command.

## What to do

1. **Locate the surface.** Find the XML layout (`res/layout/...`), its
   Activity/Fragment, and its ViewModel for `$ARGUMENTS`. Read all three.
2. **Match an existing migrated screen.** If the project already has Compose screens,
   copy the closest one's structure: `*Screen(...)` signature, how it collects state,
   how navigation callbacks are passed, how dialogs/sheets are modelled.
3. **Read the layout for parity** — capture the visual contract (spacing, colors,
   sizes, icons) before writing anything.
4. **Write the stateless screen:**
   ```kotlin
   @Composable
   fun FooScreen(
       state: FooUiState,         // or collected ViewModel values
       onBack: () -> Unit,
       /* one callback per user action */
   ) { /* design-system components only */ }
   ```
   - Compose from design-system wrappers + leaf components — no raw M3.
   - Lists → `LazyColumn`/`LazyRow` (or use `/compose-migrator:lists`).
   - Dialogs/sheets are state-driven (`var pending by remember { mutableStateOf(...) }`)
     and pinned near the end of the function.
   - Loading/empty/error states handled explicitly.
5. **Wire the host** — `ComposeView.setContent { AppTheme { FooScreen(...) } }` in the
   existing Activity/Fragment (or the nav destination if navigation is migrated). Hook
   real callbacks to the ViewModel and to navigation.
6. **Analytics/locale parity** — preserve any screen-view logging and locale handling
   the legacy screen had.

## Quality gates

- Visual parity with the XML (light + dark).
- Reuse existing icons/colors/components; no `material-icons-extended`, no raw `Color()`.
- New strings localized to all `values-*/strings.xml`.
- Verify every new symbol is imported.
- The file compiles — never leave it half-migrated.

## Output

List the files created/changed, the parity notes, and what still relies on interop.
Suggest `/compose-migrator:verify`, then `/compose-migrator:cleanup` once verified.
