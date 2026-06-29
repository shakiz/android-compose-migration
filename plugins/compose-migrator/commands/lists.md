---
description: Migrate RecyclerView/Adapter/ViewHolder lists into LazyColumn/LazyRow with item composables.
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
argument-hint: "[adapter or list screen, optional]"
---

# Compose migration — Lists

Replace RecyclerView machinery with Compose lazy layouts. Focus: `$ARGUMENTS`.

## RecyclerViews in the project

!`grep -rlE "RecyclerView|ListAdapter|RecyclerView.Adapter|ViewHolder|DiffUtil" --include=*.kt --include=*.java --include=*.xml app src 2>/dev/null | head -40`

## What to do

1. **Read the adapter + item layout.** Identify the data type, the item XML, view
   types (headers, footers, multiple row layouts), click handling, and any `DiffUtil`.
2. **Migrate the item layout** to a leaf item composable if not already done
   (`/compose-migrator:components`), parity-matched to the item XML.
3. **Replace the list:**
   ```kotlin
   LazyColumn(
       contentPadding = ...,
       verticalArrangement = Arrangement.spacedBy(spacing),
   ) {
       items(rows, key = { it.id }) { row -> RowItem(row, onClick = { onClick(row) }) }
   }
   ```
   - Provide stable `key`s (replaces `DiffUtil` identity).
   - Multiple view types → branch inside the `items` block or use separate
     `item {}` / `items {}` calls.
   - Headers/sticky headers → `stickyHeader { }` (or `item {}`).
   - Horizontal lists → `LazyRow`. Grids → `LazyVerticalGrid`.
4. **Move click/long-press** from the ViewHolder into the item composable's callbacks.
5. **Empty/loading state** handled by the screen, not the list.

## Quality gates

- Visual parity (item height, dividers, spacing) with the RecyclerView.
- Stable keys to preserve scroll/animation behavior.
- Verify imports (`androidx.compose.foundation.lazy.*`).

## Output

List the adapters retired and the lazy layouts that replace them. Note the old
adapter/ViewHolder/item XML as cleanup candidates for `/compose-migrator:cleanup`.
