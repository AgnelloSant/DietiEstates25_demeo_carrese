// src/api/auth.ts

//utilizziamo i dto da types

// src/api/auth.ts
import { http } from "./http";
import type { LoginRequest, LoginResponse, RegisterRequest, PswChangeRequest } from "@/types/user";

// 🔑 LOGIN
export const login = (payload: LoginRequest) => {
  return http.post<LoginResponse>("api/v1/user/login", payload);
};

// 📝 REGISTRAZIONE
export const register = (payload: RegisterRequest) => {
  return http.post("/v1/user/register", payload);
};

// 🔒 CAMBIO PASSWORD
export const changePassword = (payload: PswChangeRequest) => {
  return http.put("/v1/user/newpsw", payload);
};
