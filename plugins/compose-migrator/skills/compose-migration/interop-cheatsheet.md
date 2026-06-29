# Interop cheatsheet

Snippets for letting Views and Compose coexist during an incremental migration. Keep
each boundary clear and temporary.

## Compose inside an existing XML layout — `ComposeView`

In the XML layout:

```xml
<androidx.compose.ui.platform.ComposeView
    android:id="@+id/compose_root"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

In the Activity/Fragment:

```kotlin
binding.composeRoot.setContent {
    AppTheme {
        FooScreen(state = state, onBack = { finish() })
    }
}
```

Set the composition strategy on a Fragment's `ComposeView` so it disposes with the
view lifecycle:

```kotlin
composeRoot.setViewCompositionStrategy(
    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
)
```

## Legacy custom View inside Compose — `AndroidView`

```kotlin
AndroidView(
    factory = { ctx -> LegacyChartView(ctx) },
    update = { view -> view.setData(points) },
    modifier = Modifier.fillMaxWidth(),
)
```

Use this for a not-yet-migrated widget (a custom chart, an ad view, a map) so the
surrounding screen can migrate now.

## Compose inside a Fragment — `fragment-compose`

Add `androidx.fragment:fragment-compose`, then:

```kotlin
class FooFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ) = content {
        AppTheme { FooScreen(state = viewModel.state.collectAsStateWithLifecycle().value) }
    }
}
```

## Bridging observables into Compose

```kotlin
// StateFlow (preferred)
val state by viewModel.uiState.collectAsStateWithLifecycle()

// LiveData (when that's what the project uses)
val items by viewModel.items.observeAsState(emptyList())
```

## Theme during the mixed period

Keep the XML theme (`themes.xml`) and the Compose theme visually aligned so screens
don't look different depending on which system rendered them. When you migrate the
theme itself, do it once and have both systems read the same color/typography values
where possible.
