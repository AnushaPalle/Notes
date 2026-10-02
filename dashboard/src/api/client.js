// The only module that knows URLs and transport details.
const KEY = import.meta.env?.VITE_API_KEY ?? '';

async function get(path) {
  const response = await fetch(`/api${path}`, { headers: { 'X-API-Key': KEY } });
  if (!response.ok) {
    throw new Error(`${path} failed with ${response.status}`);
  }
  return response.json();
}

export const fetchEvents = (limit = 50) => get(`/events?limit=${limit}`);
export const fetchStats = () => get('/stats');

// EventSource cannot set headers, so the stream endpoint accepts the key as a query parameter (see ADR 0006).
export function openStream({ onEvent, onStatus }) {
  const source = new EventSource(`/api/stream?apiKey=${encodeURIComponent(KEY)}`);
  source.onopen = () => onStatus('open');
  source.onerror = () => onStatus(source.readyState === EventSource.CLOSED ? 'closed' : 'connecting');
  source.addEventListener('activity', (message) => onEvent(JSON.parse(message.data)));
  return () => source.close();
}
