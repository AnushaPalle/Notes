import { describe, expect, it } from 'vitest';
import { EventListController, EventListView, EventModel } from './mvc.js';
import { EventListPresenter } from './mvp.js';

const events = [
  { eventId: '1', severity: 'INFO', source: 'shop', type: 'order.created' },
  { eventId: '2', severity: 'ERROR', source: 'shop', type: 'order.failed' },
];

describe('MVC: the view observes the model, the controller only forwards input', () => {
  it('re-renders on every model change', () => {
    const writes = [];
    const model = new EventModel();
    new EventListView(model, { write: (lines) => writes.push(lines) });
    const controller = new EventListController(model);

    events.forEach((e) => controller.onEventArrived(e));
    controller.onFilterSelected('ERROR');

    expect(writes.at(-1)).toEqual(['ERROR shop/order.failed']);
    controller.onFilterSelected('WARN');
    expect(writes.at(-1)).toEqual(['no WARN events']);
  });
});

describe('MVP: the presenter decides, the passive view just displays', () => {
  it('pushes ready-made lines to a fake view', () => {
    const calls = [];
    const view = {
      showEvents: (lines) => calls.push(['events', lines]),
      showEmpty: (filter) => calls.push(['empty', filter]),
    };
    const presenter = new EventListPresenter(view);

    events.forEach((e) => presenter.onEventArrived(e));
    presenter.onFilterSelected('ERROR');
    presenter.onFilterSelected('WARN');

    expect(calls.at(-2)).toEqual(['events', ['ERROR shop/order.failed']]);
    expect(calls.at(-1)).toEqual(['empty', 'WARN']);
  });
});
