'use strict';

// Local/container wrapper that exposes the function over HTTP. In AWS the same `handler` runs behind API Gateway.
const http = require('node:http');
const { handler } = require('./handler');

const server = http.createServer((req, res) => {
  if (req.method === 'GET' && req.url === '/health') {
    res.writeHead(200, { 'content-type': 'application/json' });
    return res.end('{"status":"UP"}');
  }
  if (req.method === 'POST' && req.url === '/notify') {
    const chunks = [];
    req.on('data', (c) => chunks.push(c));
    req.on('end', async () => {
      const result = await handler({ body: Buffer.concat(chunks).toString('utf8') });
      res.writeHead(result.statusCode, result.headers);
      res.end(result.body);
    });
    return undefined;
  }
  res.writeHead(404);
  return res.end();
});

const port = Number(process.env.PORT || 3000);
server.listen(port, () => console.log(`notifier listening on ${port}`));
