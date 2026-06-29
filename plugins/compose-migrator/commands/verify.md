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
3. **Visual parity — diff against a baseline, don't just eyeball.** Establish the
   strongest comparison the environment allows, in this order:
   - **Best — render both and diff.** Capture a baseline of the legacy XML (an existing
     screenshot test, or a quick Espresso/UI Automator capture on a running
     emulator/device), then render the migrated screen's `@Preview` (or the same
     screenshot test repointed at the composable). Compare layout and styling — spacing,
     sizes, colors, corner radius, icon treatment — in light + dark. Iterate on the
     Compose until they match. Ignore string content; focus on layout. If the project
     has a screenshot-test framework (Roborazzi/Paparazzi/Showkase), wire the new
     composable into it so parity is regression-tested going forward.
   - **If no emulator/build is available** — confirm the required `@Preview` exists and
     fall back to a side-by-side checklist against the XML: spacing, sizes, colors,
     corner radius, icon treatment, light + dark. List any intentional deltas, and say
     plainly that parity was checked statically, not rendered.
4. **Build/test if available.** If the project has a wired build, run the cheapest
   meaningful check and report results honestly:
   - `./gradlew assembleDebug` (or the project's debug variant)
   - `./gradlew lint`
   - `./gradlew test`
   If no JDK/Gradle is available, say so and fall back to the static checks above —
   don't claim a build passed that you didn't run.

## Output

A pass/fail checklist per rule, how visual parity was verified (rendered diff vs. static
checklist — be explicit which), the build/test output (or a clear note that no build was
run), and any issues to fix before `/compose-migrator:cleanup`.
