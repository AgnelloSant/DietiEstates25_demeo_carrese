// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api/properties': {
        target: 'http://localhost:8082',   // dove gira Spring Boot
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/properties/, '/property-service/api/properties'),
      },
      '/api/v1/user': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      }
    },
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
    },
  },
})
