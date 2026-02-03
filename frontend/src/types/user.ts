// src/types/user.ts

// DTO inviato al backend per login
export interface LoginRequest {
  email: string
  password: string
}

export interface PublicUserDTO {
  id: number
  email: string
  role: string
  name?: string
  phone?: string
  address?: string
}

// DTO che il backend restituisce al login
export interface LoginResponse {
  accessToken: string
  user: PublicUserDTO // "ADMIN" | "USER"
}

// DTO per registrazione
export interface RegisterRequest {
  name: string
  email: string
  password: string
  phone: string
  role: string // "ADMIN" | "USER" | AGENT
}

export interface PswChangeRequest {
  email: string
  oldPsw: string
  newPsw: string
}


export interface LogoutRequest {

}
export interface UpdateProfileRequest {
  name: string
  phone: string
}

export interface UserProfile {
  id: number
  name: string
  email: string
  phone: string
  role: string
  provider: string  // "local", "google", "facebook", "github"
}