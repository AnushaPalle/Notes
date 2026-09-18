'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const { createHandler } = require('../src/handler');
const { createDelivery } = require('../src/deliver');

const body = (o) => JSON.stringify({ eventId: 'e1', source: 'billing', type: 'invoice.failed', ...o });

test('delivers an alert for an ERROR event and answers 202', async () => {
  const delivered = [];
  const handler = createHandler({ deliver: async (a) => delivered.push(a) });

  const res = await handler({ body: body({ severity: 'ERROR' }) });

  assert.equal(res.statusCode, 202);
  assert.equal(JSON.parse(res.body).alerts, 1);
  assert.equal(delivered.length, 1);
});

test('does nothing for INFO events', async () => {
  const delivered = [];
  const handler = createHandler({ deliver: async (a) => delivered.push(a) });

  const res = await handler({ body: body({ severity: 'INFO' }) });

  assert.equal(res.statusCode, 202);
  assert.equal(delivered.length, 0);
});

test('rejects malformed input with 400', async () => {
  const handler = createHandler({ deliver: async () => {} });
  assert.equal((await handler({ body: 'not json' })).statusCode, 400);
  assert.equal((await handler({ body: '{}' })).statusCode, 400);
});

test('answers 502 when delivery fails', async () => {
  const handler = createHandler({ deliver: async () => { throw new Error('down'); } });
  const res = await handler({ body: body({ severity: 'ERROR' }) });
  assert.equal(res.statusCode, 502);
});

test('delivery posts to the webhook and fails on non-2xx', async () => {
  const calls = [];
  const ok = createDelivery({ webhookUrl: 'http://hook', fetchImpl: async (url, init) => { calls.push([url, init]); return { ok: true }; } });
  await ok({ channel: 'pager', text: 'hi' });
  assert.equal(calls[0][0], 'http://hook');
  assert.deepEqual(JSON.parse(calls[0][1].body), { channel: 'pager', text: 'hi' });

  const bad = createDelivery({ webhookUrl: 'http://hook', fetchImpl: async () => ({ ok: false, status: 500 }) });
  await assert.rejects(() => bad({ channel: 'c', text: 't' }), /500/);
});
