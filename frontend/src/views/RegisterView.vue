<template>
  <header class="hero-magical">
  <div class="register-container">
    <div class="auth-card">
      
      <!-- Header -->
      <div class="register-header">
        <h1 class="page-title">Crea Account</h1>
        <p class="page-subtitle">Unisciti a Dieti Estates</p>
      </div>

      <!-- Pulsanti Social -->
      <div class="social-buttons">
        <button @click="loginWithGoogle" class="social-btn google-btn" type="button">
          <svg class="social-icon" viewBox="0 0 24 24">
            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
          </svg>
          Registrati con Google
        </button>

        <button @click="loginWithFacebook" class="social-btn facebook-btn" type="button">
          <svg class="social-icon" viewBox="0 0 24 24">
            <path fill="#1877F2" d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
          </svg>
          Registrati con Facebook
        </button>

        <button @click="loginWithGitHub" class="social-btn github-btn" type="button">
          <svg class="social-icon" viewBox="0 0 24 24">
            <path fill="#181717" d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
          </svg>
          Registrati con GitHub
        </button>
      </div>

      <!-- Divider -->
      <div class="divider">
        <span>oppure</span>
      </div>

      <!-- Form classico -->
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label>Nome completo</label>
          <input class="full-line-input" v-model="form.name" type="text" placeholder="Mario Rossi" required />
        </div>

        <div class="form-group">
          <label>Email</label>
          <input class="full-line-input" v-model="form.email" type="email" placeholder="mario@esempio.com" required />
        </div>

        <div class="form-group">
          <label>Password</label>
          <input class="full-line-input" v-model="form.password" type="password" placeholder="••••••••" required />
        </div>

        <div class="form-group">
          <label>Telefono (opzionale)</label>
          <input class="full-line-input" v-model="form.phone" type="tel" placeholder="+39 123 456 7890" />
        </div>

        <button type="submit" class="btn-primary" :disabled="registering">
          <span v-if="!registering">Crea Account</span>
          <span v-else class="loading-spinner"></span>
        </button>

        <p v-if="error" class="error-message">
          <span class="error-icon">⚠️</span>
          {{ error }}
        </p>
      </form>

      <!-- Link login -->
      <div class="login-link">
        <span>Hai già un account?</span>
        <router-link to="/login">Accedi ora</router-link>
      </div>

    </div>
  </div>
  </header>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authenticate'
import type { RegisterRequest } from '@/types/user'

const router = useRouter()
const auth = useAuthStore()

const form = reactive<RegisterRequest>({
  name: '',
  email: '',
  password: '',
  phone: '',
  role: 'USER'
})

const error = ref('')
const registering = ref(false)

const handleRegister = async () => {
  error.value = ''
  registering.value = true

  // Email Validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(form.email)) {
    error.value = 'Please enter a valid email address.';
    registering.value = false;
    return;
  }

  // Password Validation
  const passwordRegex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
  if (!passwordRegex.test(form.password)) {
    error.value = 'Password must be at least 8 characters long, contain at least one number and one uppercase letter.';
    registering.value = false;
    return;
  }

  try {
    const success = await auth.registerUser(form)
    if (success) {
      router.push('/login?registered=true')
    } else {
      error.value = 'Email già registrata o dati non validi'
    }
  } catch (e: any) {
    console.error('Errore registrazione', e)
    error.value = 'Errore durante la registrazione. Riprova.'
  } finally {
    registering.value = false
  }
}

// OAuth2 Login (registrazione automatica)
const loginWithGoogle = () => {
  window.location.href = 'http://localhost:8082/user/oauth2/authorization/google'
}

const loginWithFacebook = () => {
  window.location.href = 'https://unknowledgeable-undisconnectedly-marcela.ngrok-free.dev/oauth2/authorization/facebook'
}

const loginWithGitHub = () => {
  window.location.href = 'http://localhost:8082/user/oauth2/authorization/github'
}
</script>


