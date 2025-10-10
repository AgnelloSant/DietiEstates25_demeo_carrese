// src/api/auth.ts
import { httpUS } from "./http"
import type { 
  LoginRequest, 
  LoginResponse, 
  RegisterRequest, 
  PswChangeRequest,
  UpdateProfileRequest 
} from "@/types/user"
import { useAuthStore } from "@/stores/authenticate"
import { usePropertyStore } from "@/stores/properties"

/**
 * 🔑 LOGIN
 * POST → /api/v1/user/login
 */
export const login = (payload: LoginRequest) => {
  return httpUS.post<LoginResponse>("/login", payload)
}

/**
 * 📝 REGISTRAZIONE
 * POST → /api/v1/user/register
 */
export const register = (payload: RegisterRequest) => {
  return httpUS.post("/register", payload)
}

/**
 * 🔒 CAMBIO PASSWORD
 * PUT → /api/v1/user/newpsw
 */
export const changePassword = (payload: PswChangeRequest) => {
  return httpUS.put("/newpsw", payload)
}

/**
 * 🚪 LOGOUT
 * POST → /api/v1/user/logout
 * Pulisce token locale e chiama backend per invalidare refresh token
 */
export const logout = async () => {
  const store = useAuthStore()
  const prop = usePropertyStore()

  try {
    // Backend revoca refresh token
    await httpUS.post("/logout")
  } catch (err) {
    console.warn('Logout backend fallito (token già scaduto?)')
  }

  // Pulisci localStorage
  localStorage.removeItem("token")
  localStorage.removeItem("user")

  // Pulisci store Pinia
  store.token = null
  store.user = null
  prop.favList = []
}

/**
 * 👤 OTTIENI PROFILO COMPLETO
 * GET → /api/v1/user/profile
 * 
 * 
 */
export const getProfile = () => {
  return httpUS.get('/profile')  // ✅ Non /api/v1/user/profile!
}

/**
 * ✏️ AGGIORNA PROFILO (nome e telefono)
 * PUT → /api/v1/user/profile
 */
export const updateProfile = (data: UpdateProfileRequest) => {
  return httpUS.put('/profile', data)  
}

/**
 * ⭐ OTTIENI PREFERITI
 * GET → /api/v1/user/favourites
 */
export const getFavourites = () => {
  return httpUS.get('/favourites')
}

/**
 * ➕ AGGIUNGI PREFERITO
 * POST → /api/v1/user/addfavourite
 */
export const addFavourite = (propertyId: number) => {
  return httpUS.post('/addfavourite', { idProp: propertyId })
}
