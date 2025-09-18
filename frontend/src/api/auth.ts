// src/api/auth.ts
// Wrapper per chiamate al microservizio user-service


//utilizziamo i dto da types

// src/api/auth.ts
import { httpUS } from "./http";
import type { LoginRequest, LoginResponse, RegisterRequest, PswChangeRequest } from "@/types/user";
import { useAuthStore } from "@/stores/authenticate"
import { usePropertyStore } from "@/stores/properties";

// 🔑 LOGIN
export const login = (payload: LoginRequest) => {
  return httpUS.post<LoginResponse>("/login", payload);
  return httpUS.post<LoginResponse>("/login", payload);
};

/*
 * 📝 REGISTRAZIONE
 * POST → /api/v1/user/register
 */
export const register = (payload: RegisterRequest) => {
  return httpUS.post("/register", payload);
  return httpUS.post("/register", payload);
};

/*
 * 🔒 CAMBIO PASSWORD
 * PUT → /api/v1/user/newpsw
 */
export const changePassword = (payload: PswChangeRequest) => {
  return httpUS.put("/newpsw", payload);
  return httpUS.put("/v1/user/newpsw", payload);
};


export const logout = async () => {
  const store = useAuthStore()
  const prop = usePropertyStore()

  try {
    await httpUS.post("/logout")
  } catch (_) {}

  // Pulisci localStorage
  localStorage.removeItem("token")
  localStorage.removeItem("user")

  // Pulisci lo store
  store.token = null
  store.user = null

  prop.favList = []
}
