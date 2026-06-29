# The four quality rules

These are the rules that separate a migration that ships from one that gets reverted.
Every command and the orchestrator agent defer to this file.

## 1. Visual parity with the legacy XML

The migrated Compose UI must look like what it replaces. Before building a component
or screen, read the legacy XML it replaces and note:

- height / min-height, padding, margins
- corner radius and background (check `res/drawable/*.xml` shape drawables)
- fill color, stroke/border, elevation
- text size / style (check `styles.xml` / `themes.xml`)
- icon source and tint (`app:srcCompat`, `android:src`, `android:tint`)

Translate those to Compose using the project's theme tokens plus M3 customization
knobs (`shape`, `colors`, `heightIn`, `TextFieldDefaults.colors`, etc.). If a token
doesn't exist for a legacy value, map to the nearest semantic `colorScheme.*` slot
rather than introducing a raw `Color(...)`.

**Material 3 defaults are not the target — the existing brand look is.** A floating-
label `OutlinedTextField` is not a drop-in for a filled, fixed-height search field.
Cosmetic improvements belong in a separate follow-up PR, not the migration PR.

**Make parity verifiable, not aspirational.** Every migrated component/screen ships a
`@Preview` (light **and** dark) wrapped in the project theme — that is the artifact you
diff against the legacy UI. When an emulator or screenshot-test framework is available,
capture a baseline of the legacy XML and diff the rendered preview against it (and wire
the composable into the screenshot suite so parity stays regression-tested); when it
isn't, fall back to a side-by-side checklist and say parity was checked statically. Put
the side-by-side (legacy vs migrated, light and dark) in the PR description either way.
`/compose-migrator:verify` drives this loop.

## 2. Reuse the project's existing resources

- **Icons** — use the project's own `R.drawable.ic_*` set, which the XML already
  references via `app:srcCompat`/`android:src`. Do **not** reach for
  `androidx.compose.material.icons.filled.*` / `.outlined.*` / etc. The extended
  Material icon set isn't in `material-icons-core` and dragging in
  `material-icons-extended` bloats the APK for icons you already ship as drawables.
  Render drawables with `painterResource(R.drawable.ic_xxx)`.
- **Colors / dimens** — route through `MaterialTheme.colorScheme.*` (or the project's
  theme object). Avoid raw `Color(0xFF…)` and magic `.dp` outside the theme package.
- **Components** — if the project has a design-system wrapper (`AppButton`,
  `AppTextField`, …), use it; don't drop raw `Button`/`TextField` into screens.

A small set of unambiguous Material core icons (e.g. `Icons.AutoMirrored.Filled.ArrowBack`
for a top-bar back button) is acceptable only when no project drawable exists for it.

## 3. Verify imports

Every newly used symbol needs its `import`. This matters most in projects where you
can't run a local Gradle build — a missing import isn't caught until someone builds
in the IDE, wasting a round-trip. After each edit that introduces a new API, confirm
the import is present (grep the file). Note: formatters can strip an import added
*before* its first usage exists — add the usage first, then confirm the import
survived.

## 4. Localize new strings

The app likely supports more than one language. Add every new user-visible string to
**all** `res/values-*/strings.xml` files the project ships (e.g. `values/strings.xml`
and `values-bn/strings.xml`), not just the default locale. Reference them with
`stringResource(R.string.key)` — never hard-code display text in a composable.

## Plus: state, statelessness, and discipline

- **Hoist state.** Screens are stateless: `@Composable fun FooScreen(state, onX, onY)`.
  The ViewModel owns persistent state; transient UI state (search text, dialog
  visibility) is `remember`ed locally and pinned near the end of the composable.
- **Lifecycle-aware collection.** `collectAsStateWithLifecycle()` for `StateFlow`;
  `observeAsState()` for `LiveData`. Match what the project already uses.
- **Comments minimal** — one short line where warranted, no multi-line rationale
  blocks. Match the surrounding file.
- **Never leave a file that doesn't compile.** Half-migrated is worse than not started.
