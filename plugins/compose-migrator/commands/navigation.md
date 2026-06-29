---
description: Migrate an XML navigation graph or Activity/Intent flow to Navigation Compose with type-safe routes.
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
argument-hint: "[flow or graph, optional]"
---

# Compose migration — Navigation

Move navigation onto Navigation Compose. Do this **after** the screens in a flow are
Compose. Focus: `$ARGUMENTS`.

## Current navigation

XML nav graphs:

!`find . -path "*res/navigation*" -name "*.xml" 2>/dev/null | grep -v build`

Intent-based navigation:

!`grep -rhoE "Intent\\([^)]*::class|startActivity|setResult\\(|findNavController\\(\\)\\.navigate" --include=*.kt . 2>/dev/null | sort | uniq -c | sort -rn | head`

## What to do

1. **Map destinations & args.** From the XML graph (or the Activity/Intent flow), list
   every destination and the data it receives.
2. **Define type-safe routes** as `@Serializable` classes/objects (kotlinx-
   serialization):
   ```kotlin
   @Serializable data class FooDetail(val id: Int)
   @Serializable object Home
   ```
   - Pass **primitive ids** directly.
   - For an already-loaded object, prefer serializing it (e.g. Gson→JSON) into a route
     arg so the destination renders without an extra fetch — mirrors the old
     `putExtra(parcelable)`. Nullable arg = create mode; non-null = edit/detail mode.
3. **Build the `NavHost`** with a `composable<Route> { }` per destination wiring the
   screen + `hiltViewModel()` + navigation callbacks.
4. **Replace the old patterns:**
   - `startActivityForResult` / `setResult` → set a flag on the caller's
     `savedStateHandle` before `popBackStack()`, observed by the caller to refresh.
   - `FLAG_ACTIVITY_CLEAR_TASK` (login/logout) → a `navigateAsRoot(route)` helper that
     pops the whole back stack.
   - Deep links → `navController` deep-link handling or a slug→route mapper resolved
     once the graph is composed.
5. **Single-activity** — once a flow is fully migrated, host its `NavHost` from the
   main activity and delete the per-screen Activities.

## Quality gates

- Don't interleave an XML nav graph and Navigation Compose within one flow — migrate a
  flow wholesale or keep a clear boundary.
- Verify imports (`androidx.navigation.compose.*`, serialization).

## Output

The route catalog, the `NavHost` wiring, and replaced result/clear-task patterns.
Suggest `/compose-migrator:theme` and `/compose-migrator:cleanup` for what's left.
