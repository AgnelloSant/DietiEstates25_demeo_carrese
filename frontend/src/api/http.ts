import axios from 'axios'

// property-service (porta 8082, context-path /property-service)
export const http = axios.create({
  baseURL: '/property-service/api',
  timeout: 10000,
})

// user-service (porta 8081) — esposto via proxy /user-api
export const httpUS = axios.create({
  baseURL: '/api/v1/user',
  timeout: 10000,
})

// Interceptor minimi (puoi ampliarli in futuro)
http.interceptors.request.use(c => c)
httpUS.interceptors.request.use(c => c)
http.interceptors.response.use(r => r, e => Promise.reject(e))
httpUS.interceptors.response.use(r => r, e => Promise.reject(e))
