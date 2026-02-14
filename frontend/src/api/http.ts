import { useAuthStore } from '@/stores/authenticate'
import axios from 'axios'

// property-service (Gateway)
export const httpProperty = axios.create({
  baseURL: import.meta.env.VITE_API_PROPERTY_URL || '/properties',
  headers: {
    'Content-Type': 'application/json'
  }
});

// user-service (Gateway)
export const httpUS = axios.create({
  baseURL: import.meta.env.VITE_API_USER_URL || '/user',
  headers: {
    'Content-Type': 'application/json'
  }
});
//  INTERCEPTOR REQUEST: Aggiungi JWT 

/**
 * Aggiunge automaticamente header Authorization con JWT ad ogni richiesta
 */
const addAuthHeader = (config: any) => {
  const token = localStorage.getItem("token")
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
    console.log('JWT aggiunto alla richiesta:', config.url)
  }
  return config
}

httpProperty.interceptors.request.use(addAuthHeader)
httpUS.interceptors.request.use(addAuthHeader)

//  INTERCEPTOR RESPONSE: Gestisci 401 (token scaduto) 

/**
 * Se ricevi 401 Unauthorized:
 * 1. Prova a refreshare il token
 * 2. Se refresh OK → Riprova richiesta originale
 * 3. Se refresh FAIL → Logout e redirect a /login
 */
httpUS.interceptors.response.use(
  (response) => response,  // Se tutto OK, passa la risposta

  async (error) => {
    const originalRequest = error.config

    // Se errore è 401 e non abbiamo già provato a refreshare
    if (error.response?.status === 401 && !originalRequest._retry) {
      // Evita loop se la richiesta fallita è proprio il login
      if (originalRequest.url?.includes('/auth/login')) {
        return Promise.reject(error);
      }

      originalRequest._retry = true  // Flag per evitare loop infinito

      console.warn('Token scaduto (401), provo refresh...')

      try {
        // Chiama endpoint refresh (manda refresh token via cookie HttpOnly)
        const refreshResponse = await httpUS.post('/refresh')
        const newToken = refreshResponse.data.accessToken

        console.log('Token refreshato con successo')

        // Salva nuovo token
        localStorage.setItem('token', newToken)

        // Aggiorna header della richiesta originale
        originalRequest.headers.Authorization = `Bearer ${newToken}`

        // Riprova la richiesta originale con nuovo token
        return httpUS(originalRequest)

      } catch (refreshError) {
        // Refresh fallito → Token refresh scaduto o revocato
        console.error('Refresh fallito, logout forzato')

        // Pulisci tutto
        localStorage.clear()

        // Redirect a login
        window.location.href = '/login?session_expired=true'

        return Promise.reject(refreshError)
      }
    }

    // Se non è 401 o refresh già fallito, propaga errore
    return Promise.reject(error)
  }
)

// Property service: stesso handler 401
httpProperty.interceptors.response.use(
  (response) => response,
  async (error) => {
    // Stessa logica di httpUS (puoi estrarre in funzione shared)
    // ... (copia codice sopra)
    return Promise.reject(error)
  }
)
