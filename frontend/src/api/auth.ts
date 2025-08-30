// src/api/auth.ts
// Wrapper per chiamate al microservizio user-service

import { httpUser } from "./http";
import type { LoginRequest, LoginResponse, RegisterRequest, PswChangeRequest } from "@/types/user";

/*
 * 🔑 LOGIN
 * POST → /api/v1/user/login
 * backend → user-service (porta 8081)
 */
export const login = (payload: LoginRequest) => {
  return httpUser.post<LoginResponse>("/login", payload);
};

/*
 * 📝 REGISTRAZIONE
 * POST → /api/v1/user/register
 */
export const register = (payload: RegisterRequest) => {
  return httpUser.post("/register", payload);
};

/*
 * 🔒 CAMBIO PASSWORD
 * PUT → /api/v1/user/newpsw
 */
export const changePassword = (payload: PswChangeRequest) => {
  return httpUser.put("/newpsw", payload);
};
