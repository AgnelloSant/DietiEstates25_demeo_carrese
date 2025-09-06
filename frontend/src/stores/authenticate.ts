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
    // LOGIN
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

    // LOGOUT
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem("user")
      localStorage.removeItem("token")
    },
  },
})
