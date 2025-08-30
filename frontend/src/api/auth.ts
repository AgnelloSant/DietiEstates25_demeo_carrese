// src/api/auth.ts
// Wrapper per chiamate al microservizio user-service

import { httpUS } from "./http";
import type { LoginRequest, LoginResponse, RegisterRequest, PswChangeRequest } from "@/types/user";

/*
 * 🔑 LOGIN
 * POST → /api/v1/user/login
 * backend → user-service (porta 8081)
 */
export const login = (payload: LoginRequest) => {
  return httpUS.post<LoginResponse>("/login", payload);
};

/*
 * 📝 REGISTRAZIONE
 * POST → /api/v1/user/register
 */
export const register = (payload: RegisterRequest) => {
  return httpUS.post("/register", payload);
};

/*
 * 🔒 CAMBIO PASSWORD
 * PUT → /api/v1/user/newpsw
 */
export const changePassword = (payload: PswChangeRequest) => {
  return httpUS.put("/newpsw", payload);
};
