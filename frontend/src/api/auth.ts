// src/api/auth.ts

//utilizziamo i dto da types

// src/api/auth.ts
import { http, httpUS } from "./http";
import type { LoginRequest, LoginResponse, RegisterRequest, PswChangeRequest } from "@/types/user";
import { useAuthStore } from "@/stores/authenticate"
import { usePropertyStore } from "@/stores/properties";

// 🔑 LOGIN
export const login = (payload: LoginRequest) => {
  return httpUS.post<LoginResponse>("/login", payload);
};

// 📝 REGISTRAZIONE
export const register = (payload: RegisterRequest) => {
  return httpUS.post("/register", payload);
};

// 🔒 CAMBIO PASSWORD
export const changePassword = (payload: PswChangeRequest) => {
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
