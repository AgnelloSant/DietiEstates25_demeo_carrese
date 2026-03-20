import { useAuthStore } from '@/stores/authenticate'
import axios from 'axios'

/**
 * Builds a robust baseURL for a service.
 * 1. Takes the URL from env or uses the local fallback.
 * 2. If it's a full URL (contains ://), it ensures it ends with the service path (suffix).
 * 3. Ensures it ends with a trailing slash for proper axios path joining.
 */
const buildBaseURL = (envUrl: string | undefined, serviceSuffix: string) => {
  let url = envUrl || serviceSuffix;
  const suffix = serviceSuffix.startsWith('/') ? serviceSuffix : '/' + serviceSuffix;

  if (url.includes('://')) {
    // Ensure the URL ends with the service namespacing (e.g., /user or /properties)
    // because that's how the Gateway routes requests.
    let normalized = url.endsWith('/') ? url.slice(0, -1) : url;
    if (!normalized.endsWith(suffix)) {
      normalized += suffix;
    }
    url = normalized;
  }

  const finalUrl = url.endsWith('/') ? url : url + '/';
  console.log(`[API] Base URL configured for ${serviceSuffix}:`, finalUrl);
  return finalUrl;
};

// property-service (Gateway)
export const httpProperty = axios.create({
  baseURL: buildBaseURL(import.meta.env.VITE_API_PROPERTY_URL, '/properties'),
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
});

// user-service (Gateway)
export const httpUS = axios.create({
  baseURL: buildBaseURL(import.meta.env.VITE_API_USER_URL, '/user'),
  withCredentials: true,
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
  const requestUrl = config.url || ""

  if (
    token &&
    !requestUrl.includes('auth/login') &&
    !requestUrl.includes('auth/refresh')
  ) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
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
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (!originalRequest) {
      throw error
    }

    // Non ritentare su login o refresh
    if (
      originalRequest.url?.includes('auth/login') ||
      originalRequest.url?.includes('auth/refresh')
    ) {
      throw error
    }

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const refreshResponse = await httpUS.post('auth/refresh')
        const newToken = refreshResponse.data.accessToken

        localStorage.setItem('token', newToken)
        originalRequest.headers.Authorization = `Bearer ${newToken}`

        return httpUS(originalRequest)
      } catch (refreshError) {
        localStorage.clear()
        window.location.href = '/login?session_expired=true'
        throw refreshError
      }
    }

    throw error
  }
)


httpProperty.interceptors.response.use(
  (response) => response,
  async (error) => {
    throw error
  }
)
