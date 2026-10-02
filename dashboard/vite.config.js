import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // same-origin calls in dev, so no CORS configuration is needed on the API
    proxy: { '/api': 'http://localhost:8080' },
  },
  test: { environment: 'node' },
});
