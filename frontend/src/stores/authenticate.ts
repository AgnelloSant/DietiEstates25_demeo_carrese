import { defineStore } from "pinia"
import { login, register, changePassword } from "@/api/auth"
import type { LoginRequest, RegisterRequest, PswChangeRequest } from "@/types/user"

// Tipo coerente con la risposta del backend
interface AuthUser {
  id: number
  email: string
  role: string
  name?: string
  phone?: string
  address?: string
}

interface LoginResponse {
  accessToken: string
  user: AuthUser
}

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: (localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user")!)
      : null) as AuthUser | null,

    token: localStorage.getItem("token") || null,
  }),

  getters: { 
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    // ========== LOGIN CLASSICO (email/password) ==========
    async loginUser(payload: LoginRequest) {
      try {
        const res = await login(payload)
        const data = res.data as LoginResponse

        this.user = data.user
        this.token = data.accessToken

        localStorage.setItem("user", JSON.stringify(this.user))
        localStorage.setItem("token", this.token)

        return true
      } catch (err) {
        console.error("Login fallito", err)
        return false
      }
    },

    //LOGIN OAUTH2 (Google/Facebook) 
    /**
     * Chiamato da OAuth2Callback quando torna il token da Google/Facebook.
     * Salva il token JWT e cerca di recuperare i dati utente.
     * 
     * @param token - JWT access token ricevuto dal backend OAuth2
     */
    async loginWithOAuth(token: string) {
      try {
        // Salva il token
        this.token = token
        localStorage.setItem("token", token)

        //  decodifica JWT per estrarre info utente
        // (se il JWT contiene userId, email, role nel payload)
        try {
          const payload = JSON.parse(atob(token.split('.')[1]))
          
          // Se il JWT contiene questi campi, li usiamo
          if (payload.sub && payload.role) {
            this.user = {
              id: parseInt(payload.sub),  // userId dal JWT
              email: payload.email || '',
              role: payload.role || 'USER',
              name: payload.name || ''
            }
            localStorage.setItem("user", JSON.stringify(this.user))
          }
        } catch (decodeError) {
          console.warn("Impossibile decodificare JWT, user rimane null fino al fetch")
        }

        return true
      } catch (err) {
        console.error("Login OAuth2 fallito", err)
        return false
      }
    },

    // REGISTRAZIONE 
    async registerUser(payload: RegisterRequest) {
      try {
        await register(payload)
        return true
      } catch (err) {
        console.error("Registrazione fallita", err)
        return false
      }
    },

    // CAMBIO PASSWORD 
    async updatePassword(payload: PswChangeRequest) {
      try {
        await changePassword(payload)
        return true
      } catch (err) {
        console.error("Cambio password fallito", err)
        return false
      }
    },

    //  LOGOUT
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem("user")
      localStorage.removeItem("token")
    },

 
  },
})
