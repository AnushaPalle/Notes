# UI architecture patterns: MVC vs MVP vs MVVM

The same feature (event list with a severity filter) in three styles. Code: `dashboard/src/patterns` (MVC, MVP) and
`dashboard/src/mvvm` (the real dashboard).

| | MVC | MVP | MVVM |
|---|---|---|---|
| Who handles input | Controller | View forwards to Presenter | View binds to View-Model |
| Who updates the view | View observes Model | Presenter calls the View | Framework re-renders from View-Model state |
| View knows the Model | Yes (reads it) | No (passive) | No (only the View-Model) |
| Main testing seam | Model, Controller | Presenter with a fake view | View-Model / reducer, no UI needed |
| Fits | Server-rendered apps, classic web frameworks | Android-style, legacy UI toolkits | Reactive frameworks (React hooks, Vue, Angular, WPF) |

## Why MVVM for PulseBoard

React already re-renders from state, which is the binding MVVM needs. The hook `useDashboardViewModel` plus the pure
reducer in `dashboardState.js` holds all behaviour and is tested without a DOM. `DashboardView` is presentational.
