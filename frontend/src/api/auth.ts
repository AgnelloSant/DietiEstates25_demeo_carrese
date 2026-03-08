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
 * LOGIN
 */
export const login = (payload: LoginRequest) => {
  // Backend AuthController is mapped to /auth
  return httpUS.post<LoginResponse>("auth/login", payload)
}


export const register = (payload: RegisterRequest) => {
  return httpUS.post("auth/register", payload)
}

/**
 * CAMBIO PASSWORD
 * POST → /user/password
 */
export const changePassword = (payload: PswChangeRequest) => {
  return httpUS.post("auth/password", payload)
}

/**
 *  LOGOUT
 * POST → /user/logout 
 */
export const logout = async () => {
  const store = useAuthStore()
  const prop = usePropertyStore()

  try {
    // Backend revoca refresh token
    await httpUS.post("logout")
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
 * OTTIENI PROFILO COMPLETO
 */
export const getProfile = () => {
  return httpUS.get("me")
}

/**
 * AGGIORNA PROFILO (nome e telefono)
 */
export const updateProfile = (data: UpdateProfileRequest) => {
  const store = useAuthStore()
  return httpUS.put(`${store.user?.id}`, data)
}

export const createAdmin = (payload: RegisterRequest) => {
  return httpUS.post("create/admin", payload)
}

export const createAgent = (payload: RegisterRequest) => {
  return httpUS.post("create/agent", payload)
}
