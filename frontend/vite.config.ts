// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
server: {
  proxy: {
    '/api/properties': {
      target: 'http://localhost:8082',  // property-service
      changeOrigin: true,
    },
    '/api/v1/user': {
      target: 'http://localhost:8081',  // user-service
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
