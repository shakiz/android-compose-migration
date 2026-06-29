# Migration notes: `ProfileFragment` → `ProfileScreen`

Decision-by-decision rationale, mapped to the four quality rules.

## Structure

- **Two functions, not one.** `ProfileRoute` collects ViewModel state and passes plain
  values + callbacks into a **stateless** `ProfileScreen`. The screen has no ViewModel
  reference, so it previews and tests trivially. State is hoisted; the editable field
  values are transient UI state held with `remember(profile) { mutableStateOf(...) }`
  so they reset if the profile reloads.
- **Host.** During incremental migration, `ProfileFragment` keeps existing but its
  `onCreateView` returns a `ComposeView` whose `setContent { AppTheme { ProfileRoute(...) } }`
  renders this screen. Once navigation is migrated, `ProfileRoute` becomes a
  `composable<Profile>` destination and the Fragment is deleted in cleanup.

## Rule 1 — Visual parity

- The XML used a `MaterialToolbar` with a back icon and title → `AppTopBar` with the
  same title and `onNavigationClick`.
- Three `TextInputLayout`s with start icons and 12dp gaps → three `AppTextField`s with
  `leadingIcon` and `Arrangement.spacedBy(12.dp)`.
- A full-width 48dp primary button with a 24dp top margin → `AppButton` (whose style
  encodes the 48dp height / radius / fill) with `padding(top = 12.dp)` on top of the
  column's 12dp gap = the original 24dp.
- We did **not** switch to floating-label M3 outlined fields; `AppTextField` reproduces
  the legacy filled look. Cosmetic changes would be a separate PR.

## Rule 2 — Reuse existing resources

- Icons come from the project's own drawables via
  `painterResource(R.drawable.ic_person/ic_email/ic_phone)` — the same drawables the XML
  referenced in `app:startIconDrawable`. **No** `Icons.Filled.*` and **no**
  `material-icons-extended` dependency.
- Components are the design-system wrappers (`AppTopBar`, `AppTextField`, `AppButton`,
  `AppIcon`, `AppScaffold`) — no raw M3 `Button`/`TextField`/`TopAppBar` in the screen.
- No raw `Color(0xFF…)` — colors live in `AppTheme`.

## Rule 3 — Verify imports

- Every symbol used has an explicit import (no wildcard reliance): `Modifier`,
  `painterResource`, `stringResource`, `collectAsStateWithLifecycle`, `remember`,
  `mutableStateOf`, `Arrangement`, the design-system components, and `R`. This matters
  because a missing import here wouldn't surface until an IDE build.

## Rule 4 — Localize

- All display text uses `stringResource(R.string.*)`; nothing is hard-coded. The keys
  (`profile_title`, `profile_name`, `profile_email`, `profile_phone`, `profile_save`)
  already exist in every `values-*/strings.xml`, so no new translation work — but any
  *new* string would be added to all locales, not just the default.

## What's left for cleanup

After `/compose-migrator:verify` passes, `/compose-migrator:cleanup` removes
`fragment_profile.xml` and the `FragmentProfileBinding` usage once nothing references
them, and `ProfileFragment` is deleted when navigation moves to `ProfileRoute`.
