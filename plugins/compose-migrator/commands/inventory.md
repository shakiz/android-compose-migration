---
description: Scan the project's XML Views and produce a leaf-first migration backlog. Read-only — no edits.
allowed-tools: Read, Grep, Glob, Bash
---

# Compose migration — Inventory

You are taking stock of an Android app before migrating it from XML Views to Jetpack
Compose. **Do not edit anything.** Produce a prioritized, leaf-first backlog.

## Live project context

Layout files:

!`find . -path "*/build/*" -prune -o -name "*.xml" -path "*/res/layout*" -print 2>/dev/null | head -200`

Navigation graphs:

!`find . -path "*/build/*" -prune -o -name "*.xml" -path "*/res/navigation*" -print 2>/dev/null`

Signals (DataBinding, RecyclerView, custom views, existing Compose):

!`grep -rlE "androidx.recyclerview|<layout|ComposeView|setContent\\(|@Composable" --include=*.kt --include=*.xml --include=*.java . 2>/dev/null | grep -v '/build/' | head -100`

Stated conventions:

!`ls CLAUDE.md docs 2>/dev/null; find . -maxdepth 4 -type d -name "designsystem" -o -type d -name "theme" 2>/dev/null | grep -v build | head`

## What to do

1. **Read the conventions first** — `CLAUDE.md`, `docs/`, and any existing design-
   system/theme package. Note whether the project already has Compose screens and what
   patterns they use (StateFlow vs LiveData, design-system components, nav approach).
2. **Classify every surface**: leaf widgets / list items, list screens, form & detail
   screens, navigation flows, custom Views, themes & styles, DataBinding usage.
3. **Score each** by (a) reuse — how many screens depend on it, and (b) risk —
   animations, gestures, custom drawing, complex state.
4. **Order the backlog leaf-first**: design system & shared widgets → simple screens →
   complex screens → navigation → cleanup.

## Output

A backlog table — `Surface | Type | Depends on | Risk | Suggested command` — followed
by a recommended first 3–5 items. Call out anything that will need `AndroidView`
interop (custom Views not worth migrating yet) and any theme drift risk.

Then tell the user the natural next step is `/compose-migrator:design-system`.
