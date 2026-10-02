import { describe, expect, it } from 'vitest';
import { dashboardReducer, initialState, MAX_EVENTS, selectErrorCount, selectTotal } from './dashboardState.js';

const ev = (id, severity = 'INFO', source = 'shop', type = 'order.created') => ({ eventId: id, source, type, severity });

describe('dashboardReducer', () => {
  it('loads events and stats', () => {
    const state = dashboardReducer(initialState, {
      type: 'loaded',
      events: [ev('1')],
      stats: [{ source: 'shop', type: 'order.created', total: 5 }],
    });
    expect(state.events).toHaveLength(1);
    expect(selectTotal(state)).toBe(5);
  });

  it('prepends a streamed event and bumps the matching stat', () => {
    let state = dashboardReducer(initialState, {
      type: 'loaded',
      events: [],
      stats: [{ source: 'shop', type: 'order.created', total: 1 }],
    });
    state = dashboardReducer(state, { type: 'event/received', event: ev('2') });
    state = dashboardReducer(state, { type: 'event/received', event: ev('3', 'ERROR', 'shop', 'order.failed') });

    expect(state.events.map((e) => e.eventId)).toEqual(['3', '2']);
    expect(state.stats).toContainEqual({ source: 'shop', type: 'order.created', total: 2 });
    expect(state.stats).toContainEqual({ source: 'shop', type: 'order.failed', total: 1 });
    expect(selectErrorCount(state)).toBe(1);
  });

  it('ignores an event it already shows', () => {
    const once = dashboardReducer(initialState, { type: 'event/received', event: ev('1') });
    const twice = dashboardReducer(once, { type: 'event/received', event: ev('1') });
    expect(twice).toBe(once);
  });

  it('keeps at most MAX_EVENTS', () => {
    let state = initialState;
    for (let i = 0; i < MAX_EVENTS + 10; i += 1) {
      state = dashboardReducer(state, { type: 'event/received', event: ev(String(i)) });
    }
    expect(state.events).toHaveLength(MAX_EVENTS);
  });

  it('tracks connection and errors', () => {
    let state = dashboardReducer(initialState, { type: 'connection/changed', status: 'open' });
    state = dashboardReducer(state, { type: 'failed', message: 'boom' });
    expect(state.connection).toBe('open');
    expect(state.error).toBe('boom');
  });
});
