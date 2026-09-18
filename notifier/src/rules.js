'use strict';

// Pure, stateless rules: an event in, zero or more alerts out. Stateless is what makes this safe to run as a function.
const DEFAULT_RULES = [
  {
    name: 'any-error',
    channel: 'pager',
    match: (e) => e.severity === 'ERROR',
  },
  {
    name: 'payment-warning',
    channel: 'ops',
    match: (e) => e.severity === 'WARN' && typeof e.type === 'string' && e.type.startsWith('payment.'),
  },
];

function evaluate(event, rules = DEFAULT_RULES) {
  return rules
    .filter((rule) => rule.match(event))
    .map((rule) => ({
      rule: rule.name,
      channel: rule.channel,
      text: `[${event.severity}] ${event.source}: ${event.type} (${event.eventId})`,
    }));
}

module.exports = { evaluate, DEFAULT_RULES };
