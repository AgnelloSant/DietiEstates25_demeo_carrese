<template>
  <header class="hero-magical">
  <div class="login-container">
    <div class="auth-card">
      <!--  HEADER  -->
      <div class="login-header">
        <div class="logo-circle">
        </div>
        <h1 class="page-title">Benvenuto</h1>
        <p class="page-subtitle">Accedi a Dieti Estates</p>
      </div>

      <!--  PULSANTI SOCIAL LOGIN  -->
      <div class="auth-form">
        <!-- Pulsante Google -->
        <button @click="loginWithGoogle" class="social-btn google-btn" type="button">
          <svg class="social-icon" viewBox="0 0 24 24">
            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
          </svg>
          Continua con Google
        </button>

        <!-- Pulsante Facebook -->
        <button @click="loginWithFacebook" class="social-btn facebook-btn" type="button">
          <svg class="social-icon" viewBox="0 0 24 24">
            <path fill="#1877F2" d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
          </svg>
          Continua con Facebook
        </button>
      
        <!-- Pulsante GitHub -->
        <button @click="loginWithGitHub" class="social-btn github-btn" type="button">
          <svg class="social-icon" viewBox="0 0 24 24">
            <path fill="#181717" d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
          </svg>
          Continua con GitHub
        </button>
      </div>
      <!--  SEPARATORE  -->
      <div class="divider">
        <span>oppure</span>
      </div>

      <!--  FORM EMAIL/PASSWORD  -->
      <form @submit.prevent="handleLogin" class="form-group">
        <!-- Campo Email -->
        <div class="form-group">
          <label>Email</label>
          <div class="input-wrapper">
            
            <input 
              v-model="email" 
              type="email" 
              placeholder="nome@esempio.com" 
              required 
            />
          </div>
        </div>

        <!-- Campo Password -->
        <div class="form-group">
          <label>Password</label>
          <div class="input-wrapper">
            <input 
              v-model="password" 
              :type="showPassword ? 'text' : 'password'" 
              placeholder="Inserisci la tua password" 
              required 
            />
            <button 
              type="button" 
              class="toggle-password" 
              @click="showPassword = !showPassword"
            >
              {{ showPassword ? 'nascondi' : 'mostra' }}
            </button>
          </div>
        </div>

  
        <!-- Pulsante Login -->
        <button type="submit" class="btn-primary" :disabled="loading">
          <span v-if="!loading">Accedi</span>
          <span v-else class="loading-spinner"></span>
        </button>

        <!-- Messaggio errore -->
        <p v-if="error" class="error-message">
          <span class="error-icon">⚠️</span>
          {{ error }}
        </p>
      </form>

      <!-- Link registrazione -->
      <div class="register-link">
        <span>Non hai un account?</span>
        <router-link to="/register">Registrati ora</router-link>
      </div>
    </div>
  </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/stores/authenticate"
import { httpUS } from "@/api/http"

const router = useRouter()
const auth = useAuthStore()

const email = ref("")
const password = ref("")
const error = ref("")
const loading = ref(false)
const showPassword = ref(false)
const rememberMe = ref(false)

// LOGIN CLASSICO
const handleLogin = async () => {
  error.value = ""
  loading.value = true
  
  try {
    const success = await auth.loginUser({ 
      email: email.value, 
      password: password.value 
    })
    
    if (success) {
      router.push("/")
    } else {
      error.value = "Email o password non validi"
    }
  } catch (err) {
    error.value = "Errore durante il login. Riprova."
  } finally {
    loading.value = false
  }
}

//  GOOGLE - Con prompt nell'URL
const loginWithGoogle = () => {
  console.log('🔵 Redirecting to Google OAuth2...')
  // Costruisci URL con parametri custom
  const baseUrl = 'http://localhost:8082/user/oauth2/authorization/google'
  const params = new URLSearchParams({
    prompt: 'select_account'  // ← Forza scelta account
  })
  window.location.href = `${baseUrl}?${params.toString()}`
}

//  FACEBOOK - Con auth_type nell'URL
const loginWithFacebook = () => {
  console.log('🔵 Redirecting to Facebook OAuth2...')
  const baseUrl = httpUS.defaults.baseURL + "oauth2/authorization/facebook"
  const params = new URLSearchParams({
    auth_type: 'reauthenticate'  // ← Forza re-login
  })
  window.location.href = `${baseUrl}?${params.toString()}`
}
//login con github utilizzando l authorization callback url 
const loginWithGitHub = () => {
  console.log('🔵 Redirecting to GitHub OAuth2...')
  window.location.href = 'http://localhost:8082/user/oauth2/authorization/github'
}
</script>

