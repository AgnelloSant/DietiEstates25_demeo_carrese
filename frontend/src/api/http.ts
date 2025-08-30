// src/api/http.ts
// Configura le istanze Axios per i microservizi (property e user)

import axios from "axios";

/*
 * 🔹 Property Service
 * Tutte le chiamate relative agli immobili (ricerca, creazione, update, delete)
 * Vanno su http://localhost:8082/property-service/api → proxato da Vite
 */
export const httpProperty = axios.create({
  baseURL: "/property-service/api", // Vite proxy → backend property-service (8082)
  timeout: 10000,
});

/*
 * 🔹 User Service
 * Tutte le chiamate relative all’autenticazione e agli utenti
 * Vanno su http://localhost:8081/api/v1/user → proxato da Vite
 */
export const httpUser = axios.create({
  baseURL: "/api/v1/user", // Vite proxy → backend user-service (8081)
  timeout: 10000,
});

/*
 * Interceptor comuni → puoi aggiungere token JWT in futuro
 */
function setupInterceptors(instance: any) {
  instance.interceptors.request.use((config: any) => {
    // es: const token = localStorage.getItem("token")
    // if (token) config.headers.Authorization = `Bearer ${token}`
    return config;
  });

  instance.interceptors.response.use(
    (res: any) => res,
    (err: any) => {
      console.error("API error:", err?.response ?? err);
      return Promise.reject(err);
    }
  );
}

// Attacco gli interceptor a entrambe le istanze
setupInterceptors(httpProperty);
setupInterceptors(httpUser);
