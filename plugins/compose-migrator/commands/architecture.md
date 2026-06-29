---
description: Set up the Compose architecture — state hoisting, the ViewModel→UI bridge, the Compose host, and the View↔Compose interop boundary.
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
---

# Compose migration — Architecture

Establish the structural patterns every migrated screen will follow, so screens stay
consistent and testable.

## How the project currently bridges state

ViewModel state holders in use:

!`grep -rhoE "StateFlow<|MutableStateFlow|LiveData<|MutableLiveData|mutableStateOf" --include=*.kt . 2>/dev/null | sort | uniq -c | sort -rn | head`

Existing Compose hosts:

!`grep -rlE "setContent\\(|ComposeView|collectAsStateWithLifecycle|observeAsState" --include=*.kt . 2>/dev/null | grep -v '/build/' | head -40`

## What to do

1. **Adopt the project's existing bridge** rather than rewriting the data layer:
   - `StateFlow` → collect with `collectAsStateWithLifecycle()`.
   - `LiveData` → collect with `observeAsState(default)`.
   Pick whichever the ViewModels already expose; don't convert them mid-migration.
2. **Define the stateless screen contract.** Every screen becomes:
   ```kotlin
   @Composable
   fun FooScreen(
       state: FooUiState,          // or collected ViewModel values
       onBack: () -> Unit,
       onAction: (FooAction) -> Unit,
   )
   ```
   State is hoisted to the ViewModel; transient UI state (search text, dialog
   visibility) is `remember`ed locally.
3. **Set up the host.** Either a `ComposeView` inside the existing Activity/Fragment
   (incremental — preferred), or, if the project is going single-activity, a
   `BaseComposeActivity` that provides theme + cross-cutting concerns (locale,
   connectivity, analytics, toasts) via `CompositionLocal`s.
4. **Define the interop boundary** for not-yet-migrated custom Views (`AndroidView`)
   so screens can migrate before every widget does. See the skill's
   `interop-cheatsheet.md`.

## Output

Describe the chosen state bridge, the screen contract, and the host/interop pattern
(with file paths for anything scaffolded). Suggest `/compose-migrator:components`
next.
