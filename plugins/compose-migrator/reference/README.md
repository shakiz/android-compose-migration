# Worked example: migrating `ProfileFragment`

A small, self-contained before→after migration of a single screen, so you can see the
four quality rules applied rather than just described. It's a generic profile screen
(name, email, phone, an editable form, a save button) — not tied to any real app.

```
reference/
├── before/
│   ├── fragment_profile.xml     # the legacy layout
│   └── ProfileFragment.kt       # the View-system Fragment + findViewById wiring
└── after/
    ├── ProfileScreen.kt         # the migrated stateless @Composable + host
    └── notes.md                 # decision-by-decision rationale vs. the rules
```

## How to read it

1. Start with `before/` — note the visual contract: a top app bar, three labelled
   fields with leading icons, a filled rounded "Save" button.
2. Read `after/ProfileScreen.kt` — a stateless `ProfileScreen(state, onBack, onSave)`,
   state hoisted, design-system-style components, icons via `painterResource`, strings
   via `stringResource`.
3. Read `after/notes.md` — why each choice was made, mapped to **visual parity**,
   **reuse existing resources**, **verified imports**, and **localization**.

This example assumes the project already ran `/compose-migrator:design-system` (so
`AppTheme`, `AppButton`, `AppTextField`, `AppTopBar`, `AppIcon` exist). In a real
project, reuse *that project's* equivalents rather than these names.
