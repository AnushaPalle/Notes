'use strict';

// Delivery is injected so the handler can be tested without the network.
function createDelivery({ webhookUrl, fetchImpl = globalThis.fetch, log = console } = {}) {
  return async function deliver(alert) {
    if (!webhookUrl) {
      log.info(`ALERT (no webhook configured) ${alert.channel}: ${alert.text}`);
      return;
    }
    const response = await fetchImpl(webhookUrl, {
      method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({ channel: alert.channel, text: alert.text }),
    });
    if (!response.ok) {
      throw new Error(`webhook responded ${response.status}`);
    }
  };
}

module.exports = { createDelivery };
