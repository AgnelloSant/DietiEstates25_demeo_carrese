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
<<<<<<< HEAD
  accessToken: string
  user: PublicUserDTO // "ADMIN" | "USER"
=======
  id: string
  name: string
  email: string
  phone: string
  role: string // "ADMIN" | "USER"
>>>>>>> main-pulito
}

// DTO per registrazione
export interface RegisterRequest {
  name: string
  email: string
  password: string
  phone: string
  role: string // "ADMIN" | "USER"
}

// DTO per cambio password
export interface PswChangeRequest {
  email: string
  oldPsw: string
  newPsw: string
}


export interface LogoutRequest{ 
  
}
