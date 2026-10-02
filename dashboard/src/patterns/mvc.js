// MVC, framework-free, for comparison with MVVM and MVP (same feature: an event list with a severity filter).
//
// Data flow: user -> Controller -> Model --notifies--> View (the View reads the Model directly).

export class EventModel {
  #events = [];
  #filter = 'ALL';
  #listeners = [];

  subscribe(listener) {
    this.#listeners.push(listener);
  }

  add(event) {
    this.#events.unshift(event);
    this.#notify();
  }

  setFilter(filter) {
    this.#filter = filter;
    this.#notify();
  }

  get filter() {
    return this.#filter;
  }

  get visible() {
    return this.#filter === 'ALL' ? [...this.#events] : this.#events.filter((e) => e.severity === this.#filter);
  }

  #notify() {
    this.#listeners.forEach((listener) => listener());
  }
}

// `output` is anything with write(lines); in a browser it would wrap a DOM node.
export class EventListView {
  constructor(model, output) {
    this.model = model;
    this.output = output;
    model.subscribe(() => this.render()); // the view observes the model
  }

  render() {
    const lines = this.model.visible.map((e) => `${e.severity} ${e.source}/${e.type}`);
    this.output.write(lines.length ? lines : [`no ${this.model.filter} events`]);
  }
}

export class EventListController {
  constructor(model) {
    this.model = model;
  }

  onEventArrived(event) {
    this.model.add(event);
  }

  onFilterSelected(filter) {
    this.model.setFilter(filter);
  }
}
