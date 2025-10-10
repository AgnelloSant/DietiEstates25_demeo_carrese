<template>
  <div class="callback-container">
    <div class="loading">
      <!-- Spinner animato che gira -->
      <div class="spinner"></div>
      
      <!-- Messaggio di caricamento -->
      <p v-if="!error">Completamento accesso...</p>
      <p v-else class="error-text">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authenticate'

// Router per navigare tra le pagine
const router = useRouter()

// Store di autenticazione (gestisce user e token)
const auth = useAuthStore()

// Messaggio di errore (se qualcosa va storto)
const error = ref('')

/**
 * Questa funzione viene eseguita automaticamente quando la pagina si carica.
 * È chiamata dal backend dopo che Google/Facebook hanno autenticato l'utente.
 * 
 * Flusso:
 * 1. Backend fa login OAuth2 con Google/Facebook
 * 2. Backend genera JWT
 * 3. Backend redirect qui con: /oauth2/callback?token=eyJhbGc...
 * 4. Noi leggiamo il token dall'URL
 * 5. Salviamo il token nello store
 * 6. Redirect alla home
 */
onMounted(async () => {
  console.log('🔵 OAuth2Callback: Pagina caricata')
  
  // Legge i parametri dall'URL (es: ?token=abc123&error=xyz)
  const params = new URLSearchParams(window.location.search)
  const token = params.get('token')         // Token JWT dal backend
  const errorParam = params.get('error')    // Eventuale errore
  
  // CASO 1: C'è un errore nell'URL
  if (errorParam) {
    console.error('❌ OAuth2 error parameter:', errorParam)
    error.value = 'Login fallito. Riprova.'
    
    // Dopo 2 secondi torna al login
    setTimeout(() => {
      router.push('/login?error=' + errorParam)
    }, 2000)
    return
  }
  
  // CASO 2: Token presente → Login riuscito
  if (token) {
    console.log('✅ OAuth2 token ricevuto:', token.substring(0, 20) + '...')
    
    try {
      // Salva token e user nello store
      await auth.loginWithOAuth(token)
      
      console.log('✅ Login OAuth2 completato, redirect alla home...')
      
      // Redirect alla home dopo 500ms (per mostrare brevemente lo spinner)
      setTimeout(() => {
        router.push('/')
      }, 500)
      
    } catch (err) {
      console.error('❌ Errore durante salvataggio token:', err)
      error.value = 'Errore durante il login. Riprova.'
      
      setTimeout(() => {
        router.push('/login?error=token_save_failed')
      }, 2000)
    }
    
  } 
  // CASO 3: Né token né errore → Qualcosa è andato storto
  else {
    console.error('❌ OAuth2 callback senza token né errore')
    error.value = 'Risposta non valida dal server.'
    
    setTimeout(() => {
      router.push('/login?error=invalid_callback')
    }, 2000)
  }
})
</script>

<style scoped>
/* Container che occupa tutta la pagina */
.callback-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* Box centrale con spinner e testo */
.loading {
  text-align: center;
  color: white;
  padding: 2rem;
}

/* Spinner che gira (animazione CSS) */
.spinner {
  width: 60px;
  height: 60px;
  border: 5px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1.5rem;
}

/* Animazione rotazione */
@keyframes spin {
  to { 
    transform: rotate(360deg); 
  }
}

/* Testo normale */
.loading p {
  font-size: 1.2rem;
  font-weight: 500;
  margin: 0;
}

/* Testo di errore (rosso) */
.error-text {
  color: #ffcccc;
  font-weight: 600;
}
</style>
