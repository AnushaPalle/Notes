'use strict';

const { evaluate, DEFAULT_RULES } = require('./rules');
const { createDelivery } = require('./deliver');

const json = (statusCode, body) => ({
  statusCode,
  headers: { 'content-type': 'application/json' },
  body: JSON.stringify(body),
});

// Lambda/API Gateway style: (httpEvent) => { statusCode, body }
function createHandler({ deliver, rules = DEFAULT_RULES }) {
  return async function handler(httpEvent) {
    let event;
    try {
      event = JSON.parse(httpEvent.body || '');
    } catch {
      return json(400, { error: 'body must be JSON' });
    }
    if (!event || typeof event.eventId !== 'string' || typeof event.type !== 'string') {
      return json(400, { error: 'eventId and type are required' });
    }

    const alerts = evaluate(event, rules);
    try {
      await Promise.all(alerts.map(deliver));
    } catch (error) {
      // 502 tells the caller delivery failed; the core treats it as a non-fatal publisher error
      return json(502, { error: 'delivery failed', detail: error.message });
    }
    return json(202, { alerts: alerts.length });
  };
}

const handler = createHandler({ deliver: createDelivery({ webhookUrl: process.env.ALERT_WEBHOOK_URL }) });

module.exports = { createHandler, handler };
