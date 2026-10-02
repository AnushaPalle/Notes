// MVP, framework-free: the same feature as mvc.js.
//
// Data flow: user -> View -> Presenter -> Model, and Presenter -> View. The View is passive (no logic, does not know
// the Model) and the Model does not know anybody. All decisions live in the Presenter, so it is testable with a fake view.

export class EventListPresenter {
  #events = [];
  #filter = 'ALL';

  // `view` must implement showEvents(lines) and showEmpty(filter)
  constructor(view) {
    this.view = view;
  }

  onEventArrived(event) {
    this.#events.unshift(event);
    this.#refresh();
  }

  onFilterSelected(filter) {
    this.#filter = filter;
    this.#refresh();
  }

  #refresh() {
    const visible = this.#filter === 'ALL' ? this.#events : this.#events.filter((e) => e.severity === this.#filter);
    if (visible.length === 0) {
      this.view.showEmpty(this.#filter);
    } else {
      this.view.showEvents(visible.map((e) => `${e.severity} ${e.source}/${e.type}`));
    }
  }
}
