// src/stores/authenticate.ts
// Store Pinia per gestire autenticazione utente (login, logout, registrazione, cambio psw)

import { defineStore } from "pinia";
// Importiamo le API che parlano col backend
import { login, register, changePassword } from "@/api/auth";
// Importiamo i tipi DTO che rappresentano le request/response del backend
import type { LoginRequest, LoginResponse, RegisterRequest, PswChangeRequest } from "@/types/user";

export const useAuthStore = defineStore("auth", {
  // ------------------ STATO ------------------
  state: () => ({
    // Oggetto utente loggato (torna dal backend come LoginResponse) oppure null se non autenticato
    user: null as LoginResponse | null,

    // Token JWT in futuro. Per ora mettiamo un "dummy" o null
    token: null as string | null,
  }),

  // ------------------ AZIONI ------------------
  actions: {
    // LOGIN → chiama il backend passando l’oggetto LoginRequest
    async loginUser(payload: LoginRequest) {
      try {
        // Chiamo la API con il payload (email, password)
        const res = await login(payload);

        // Salvo i dati utente ritornati (LoginResponse)
        this.user = res.data;

        // Salvo un token fittizio (in futuro potresti gestire JWT)
        this.token = "dummy";

        return true;
      } catch (err) {
        console.error("Login fallito", err);
        return false;
      }
    },

    // REGISTRAZIONE → invia RegisterRequest al backend
    async registerUser(payload: RegisterRequest) {
      try {
        await register(payload); // basta inviare i dati al backend
        return true;
      } catch (err) {
        console.error("Registrazione fallita", err);
        return false;
      }
    },

    // CAMBIO PASSWORD → invia PswChangeRequest al backend
    async updatePassword(payload: PswChangeRequest) {
      try {
        await changePassword(payload);
        return true;
      } catch (err) {
        console.error("Cambio password fallito", err);
        return false;
      }
    },

    // LOGOUT → svuota lo stato utente e token
    logout() {
      this.user = null;
      this.token = null;
    },
  },
});
