// Pure state logic of the dashboard: no React, no network. This is the "Model" half of the view-model.
export const MAX_EVENTS = 50;

export const initialState = { events: [], stats: [], connection: 'connecting', error: null };

function bump(stats, event) {
  const found = stats.some((s) => s.source === event.source && s.type === event.type);
  if (!found) {
    return [...stats, { source: event.source, type: event.type, total: 1 }];
  }
  return stats.map((s) =>
    s.source === event.source && s.type === event.type ? { ...s, total: s.total + 1 } : s,
  );
}

export function dashboardReducer(state, action) {
  switch (action.type) {
    case 'loaded':
      return { ...state, events: action.events.slice(0, MAX_EVENTS), stats: action.stats, error: null };
    case 'event/received': {
      if (state.events.some((e) => e.eventId === action.event.eventId)) {
        return state; // already shown (initial load and stream can overlap)
      }
      return {
        ...state,
        events: [action.event, ...state.events].slice(0, MAX_EVENTS),
        stats: bump(state.stats, action.event),
      };
    }
    case 'connection/changed':
      return { ...state, connection: action.status };
    case 'failed':
      return { ...state, error: action.message };
    default:
      return state;
  }
}

export const selectErrorCount = (state) => state.events.filter((e) => e.severity === 'ERROR').length;
export const selectTotal = (state) => state.stats.reduce((sum, s) => sum + s.total, 0);
