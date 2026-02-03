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
 * POST → /user/auth/login
 */
export const login = (payload: LoginRequest) => {
  // Backend AuthController is mapped to /auth
  return httpUS.post<LoginResponse>("/auth/login", payload)
}

/**
 * 📝 REGISTRAZIONE
 * POST → /user/auth/register
 */
export const register = (payload: RegisterRequest) => {
  return httpUS.post("/auth/register", payload)
}

/**
 * 🔒 CAMBIO PASSWORD
 * POST → /user/password
 */
export const changePassword = (payload: PswChangeRequest) => {
  return httpUS.post("/password", payload)
}

/**
 * 🚪 LOGOUT
 * POST → /user/logout (Note: Backend may not handle this, kept for client consistency)
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
 * GET → /user/me
 */
export const getProfile = () => {
  return httpUS.get("/me")
}

/**
 * ✏️ AGGIORNA PROFILO (nome e telefono)
 * PUT → /user/{id}
 */
export const updateProfile = (id: number, data: UpdateProfileRequest) => {
  return httpUS.put(`/${id}`, data)
}
