---
description: Verify a migration — imports resolve, visual parity holds, and the build/tests pass if a build is available.
allowed-tools: Read, Grep, Glob, Bash
argument-hint: "[file or screen, optional]"
---

# Compose migration — Verify

Confirm the migration is correct before cleanup. Focus: `$ARGUMENTS` (if empty, verify
the most recently changed Compose files).

## What to do

1. **Imports resolve.** For each changed Kotlin file, confirm every newly used symbol
   has a matching `import`. This is the most common silent failure when no local build
   runs. Grep the file for the symbols you introduced and check the import block.
2. **No regressions to the rules:**
   - No `androidx.compose.material.icons.filled.*` / `.outlined.*` in screen code
     (search for it). Icons should be `painterResource(R.drawable.ic_*)`.
   - No raw `Color(0xFF…)` or stray magic `.dp` in screens.
   - New user-visible strings exist in **all** `values-*/strings.xml` locales.
3. **Visual parity checklist** — compare the migrated screen against the legacy XML:
   spacing, sizes, colors, corner radius, icon treatment, light + dark. List any
   intentional deltas.
4. **Build/test if available.** If the project has a wired build, run the cheapest
   meaningful check and report results honestly:
   - `./gradlew assembleDebug` (or the project's debug variant)
   - `./gradlew lint`
   - `./gradlew test`
   If no JDK/Gradle is available, say so and fall back to the static checks above —
   don't claim a build passed that you didn't run.

## Output

A pass/fail checklist per rule, the build/test output (or a clear note that no build
was run), and any issues to fix before `/compose-migrator:cleanup`.
