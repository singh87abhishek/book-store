import { defineConfig } from 'vite'

// Proxy /bookstore-api requests to the backend to avoid CORS in dev
export default defineConfig({
  server: {
    proxy: {
      '/bookstore-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path, // keep the path as-is
      },
    },
  },
})
