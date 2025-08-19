//Configura l’istanza Axios (baseURL relativa: /api). In dev Vite la proxerà al backend.
// src/api/http.ts
import axios from 'axios'

// 1) Crea istanza Axios con baseURL
//    In sviluppo: /api → proxata a http://localhost:8082 (vite.config.ts)
//    In produzione: configura l'hosting/reverse proxy per instradare /api al backend

export const http = axios.create({
  baseURL: '/property-service/api', // 👈 aggiunto prefisso corretto
  timeout: 10000,
})

// 2) Interceptor di richiesta (es. aggiungere token se in futuro farai auth)
http.interceptors.request.use((config) => {
  // es: const token = localStorage.getItem('token')
  // if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 3) Interceptor di risposta (gestione errori centralizzata)
http.interceptors.response.use(
  (res) => res,
  (err) => {
    console.error('API error:', err?.response ?? err)
    return Promise.reject(err)
  }
)