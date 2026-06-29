# Bottom-up migration strategy

The recommended approach (per Android's official guidance) is **gradual, incremental
adoption via interop** — never a big-bang rewrite. Start at the leaves and work up,
keeping the app shippable at every commit.

## Why bottom-up

- **Lower risk** — a migrated button or row is trivial to verify; a migrated whole
  navigation graph is not.
- **Compounding reuse** — once the design system and leaf components exist, screens
  assemble quickly from parts you already trust.
- **Reviewable PRs** — one layer / one screen per PR stays small and reviewable.

Top-down (start at the Activity/screen level) is only worth it when you're already
refactoring an entire nav flow and the leaves are throwaway.

## Layer order and the command that drives each

| # | Layer | Command | Done when |
|---|---|---|---|
| 1 | Inventory | `/inventory` | You have a leaf-first backlog and know the risks. |
| 2 | Design system | `/design-system` | Theme tokens + core component wrappers exist. |
| 3 | Architecture | `/architecture` | State hoisting pattern + host + interop boundary set. |
| 4 | Leaf components | `/components` | Reusable widgets are parity-matched composables. |
| 5 | Screens | `/screen <name>` | The screen is a stateless `*Screen(...)` + host wiring. |
| 6 | Lists | `/lists` | RecyclerViews are `LazyColumn`/`LazyRow`. |
| 7 | Navigation | `/navigation` | Flows run on Navigation Compose. |
| 8 | Theme | `/theme` | XML theme fully mapped to Compose; M2→M3 aligned. |
| 9 | Cleanup | `/cleanup` | Dead XML / adapters / DataBinding removed. |

You don't have to finish a layer app-wide before starting the next for a given
surface — but for any single surface, respect the dependency order (a screen needs
its leaf components and the design system first).

## Interop is the safety net

The interop APIs let Views and Compose coexist during the migration:

- **`ComposeView`** in an XML layout → host a `@Composable` inside an existing
  Activity/Fragment. The usual entry point for migrating one screen at a time.
- **`AndroidView`** in Compose → embed a legacy custom `View` inside a composable
  (`factory = { ctx -> LegacyView(ctx) }`) when a widget isn't migrated yet.
- **`androidx.fragment:fragment-compose`** → `Fragment` whose content is Compose.

See `interop-cheatsheet.md` for snippets. The rule: each interop boundary should be
clear and temporary — don't interleave XML and Compose within the same widget.

## Common pitfalls

- Migrating too much at once (the big-bang trap).
- Re-styling the app to Material 3 defaults instead of preserving the brand look.
- Forgetting lifecycle-aware collection (use `collectAsStateWithLifecycle`).
- Mixing an XML nav graph and Navigation Compose in the same flow without a clear
  boundary.
- Leaving theme drift between the XML theme and the Compose theme during the mixed
  period — keep them visually aligned.
