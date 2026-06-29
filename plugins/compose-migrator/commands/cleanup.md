---
description: After a surface is fully Compose, delete the dead XML layouts, adapters, DataBinding, and orphaned resources.
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
argument-hint: "[surface or layout, optional]"
---

# Compose migration — Cleanup

Remove what the migration made dead. **Only delete a thing after confirming nothing
references it.** Focus: `$ARGUMENTS`.

## What to do

1. **Confirm the surface is fully Compose** — no remaining `ComposeView` interop or
   `AndroidView` wrapping a legacy View for it. If interop remains, it's not ready.
2. **Find references before deleting.** For each candidate (an item XML, an adapter, a
   layout, a `@Bindable` field, a custom View), grep the codebase for references. Only
   remove it when there are none.
   ```bash
   grep -rn "R.layout.foo_item\|FooAdapter\|FooViewHolder" --include=*.kt --include=*.xml .
   ```
3. **Delete in dependency order:** screen layout XML → item layouts → adapters /
   ViewHolders → `DataBinding`/`@Bindable` plumbing for that screen → now-orphaned
   `res/drawable` / `res/menu` used only by the old layout.
4. **Drop dead dependencies** only when *no* surface uses them anymore — e.g. disable
   DataBinding/viewBinding in `build.gradle`, remove RecyclerView, only once the last
   consumer is gone. Be conservative; leave shared deps.
5. **Don't delete what you didn't migrate** — if a resource's purpose contradicts the
   "dead" assumption (still referenced, used by another flavor/locale), stop and flag
   it instead of removing it.

## Quality gates

- Every deletion preceded by a clean reference check.
- The project still builds (`/compose-migrator:verify`) after cleanup.

## Output

The list of files/resources removed (with the reference check that justified each) and
anything left in place deliberately with the reason.
