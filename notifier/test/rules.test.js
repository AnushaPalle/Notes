'use strict';

const test = require('node:test');
const assert = require('node:assert/strict');
const { evaluate } = require('../src/rules');

const base = { eventId: 'e1', source: 'billing', type: 'invoice.failed' };

test('ERROR events page someone', () => {
  const alerts = evaluate({ ...base, severity: 'ERROR' });
  assert.equal(alerts.length, 1);
  assert.equal(alerts[0].channel, 'pager');
  assert.match(alerts[0].text, /billing: invoice\.failed/);
});

test('WARN on payment.* goes to ops, other WARN is ignored', () => {
  assert.equal(evaluate({ ...base, type: 'payment.slow', severity: 'WARN' })[0].channel, 'ops');
  assert.deepEqual(evaluate({ ...base, type: 'cache.miss', severity: 'WARN' }), []);
});

test('INFO never alerts', () => {
  assert.deepEqual(evaluate({ ...base, severity: 'INFO' }), []);
});
