// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  base: '/',
  plugins: [vue()],
  server: {
    proxy: {
      '/properties': {
        target: 'http://api-gateway:8082',  // property-service (Gateway)
        changeOrigin: true,
      },
      '/user': {
        target: 'http://api-gateway:8082',  // user-service (Gateway)
        changeOrigin: true,
      }
    },
  },

  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
    },
  },
  build: {
    outDir: 'dist',  // ✅ Output va SOLO in dist
    emptyOutDir: true
  }
})
