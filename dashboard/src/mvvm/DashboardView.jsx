import React from 'react';

// Presentational only: everything arrives through props, nothing is fetched or computed here.
export function DashboardView({ events, stats, connection, error, total, errorCount }) {
  return (
    <main>
      <header>
        <h1>PulseBoard</h1>
        <span className={`badge ${connection}`}>{connection}</span>
      </header>

      {error && <p className="error">{error}</p>}

      <section className="cards">
        <div className="card"><strong>{total}</strong>events seen</div>
        <div className="card"><strong>{errorCount}</strong>errors in view</div>
        {stats.map((s) => (
          <div className="card" key={`${s.source}/${s.type}`}>
            <strong>{s.total}</strong>
            {s.source} / {s.type}
          </div>
        ))}
      </section>

      <table>
        <thead>
          <tr><th>Time</th><th>Source</th><th>Type</th><th>Severity</th></tr>
        </thead>
        <tbody>
          {events.map((e) => (
            <tr key={e.eventId}>
              <td>{new Date(e.occurredAt).toLocaleTimeString()}</td>
              <td>{e.source}</td>
              <td>{e.type}</td>
              <td className={`sev-${e.severity}`}>{e.severity}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </main>
  );
}
