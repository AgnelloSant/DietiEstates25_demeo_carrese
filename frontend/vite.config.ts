import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const proxyTarget = env.VITE_PROXY_TARGET || 'http://api-gateway:8082';

  return {
    plugins: [vue()],
    server: {
      proxy: {
        '/properties': {
          target: proxyTarget,
          changeOrigin: true,
        },
        '/user': {
          target: proxyTarget,
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
  }
})
