import { useEffect, useReducer } from 'react';
import { fetchEvents, fetchStats, openStream } from '../api/client.js';
import { dashboardReducer, initialState, selectErrorCount, selectTotal } from './dashboardState.js';

// MVVM: the view-model owns state and behaviour and exposes ready-to-render values. The view only binds to them.
export function useDashboardViewModel() {
  const [state, dispatch] = useReducer(dashboardReducer, initialState);

  useEffect(() => {
    let active = true;
    Promise.all([fetchEvents(), fetchStats()])
      .then(([events, stats]) => active && dispatch({ type: 'loaded', events, stats }))
      .catch((error) => active && dispatch({ type: 'failed', message: error.message }));

    const close = openStream({
      onEvent: (event) => dispatch({ type: 'event/received', event }),
      onStatus: (status) => dispatch({ type: 'connection/changed', status }),
    });
    return () => {
      active = false;
      close();
    };
  }, []);

  return {
    events: state.events,
    stats: state.stats,
    connection: state.connection,
    error: state.error,
    total: selectTotal(state),
    errorCount: selectErrorCount(state),
  };
}
