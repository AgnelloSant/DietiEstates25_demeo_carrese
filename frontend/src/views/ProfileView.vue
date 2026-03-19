<template>
  <div class="max-page-container">
    <!-- Header con Avatar -->
    <div class="page-header">
      <div class="avatar-circle">
        <span class="avatar-text">{{ initials }}</span>
      </div>
      <h1 class="page-title">Il tuo Profilo</h1>
      <p class="page-subtitle">Gestisci le tue informazioni personali</p>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Caricamento...</p>
    </div>

    <!-- Profilo caricato -->
    <div v-else class="multi-content">
      
      <!-- Card Info Utente -->
      <div class="basic-card">
        <div class="card-header">
          <h2>Informazioni Personali</h2>
          <button 
            v-if="!editMode" 
            @click="enableEdit" 
            class="btn-secondary"
          >
             Modifica
          </button>
        </div>

        <div class="card-content">
          <div class="card-item">
            <label> Nome</label>
            <input 
              v-if="editMode" 
              v-model="editForm.name" 
              type="text" 
              placeholder="Inserisci il tuo nome"
              class="edit-input"
            />
            <p v-else class="card-item">{{ profile.name || 'Non specificato' }}</p>
          </div>

          <div class="card-item">
            <label> Email</label>
            <p class="card-item">{{ profile.email }}</p>
            <span class="badge" :class="providerClass">{{ providerLabel }}</span>
          </div>

          <div class="card-item">
            <label> Telefono</label>
            <input 
              v-if="editMode" 
              v-model="editForm.phone" 
              type="tel" 
              placeholder="+39 123 456 7890"
              class="edit-input"
            />
            <p v-else class="card-item">{{ profile.phone || 'Non specificato' }}</p>
          </div>

          <div class="card-item">
            <label> Ruolo</label>
            <p class="card-item">
              <span class="role-badge">{{ profile.role }}</span>
            </p>
          </div>
        </div>

        <!-- Pulsanti edit mode -->
        <div v-if="editMode" class="centred-line">
          <button @click="saveProfile" class="btn-principal" :disabled="saving">
            <span v-if="!saving">Salva</span>
            <span v-else>⏳ Salvataggio...</span>
          </button>
          <button @click="cancelEdit" class="btn-secondary">❌Annulla</button>
        </div>
      </div>

      <!-- Cambio Password  solo se loggati con email e password-->
      <div v-if="profile.provider === 'local'" class="basic-card">
        <div class="card-header">
          <h2>Sicurezza</h2>
        </div>

        <form @submit.prevent="handleChangePassword" class="password-form">
          <div class="form-group">
            <label>Vecchia password</label>
            <input
              v-model="oldPsw"
              type="password"
              placeholder="••••••••"
              required
            />
          </div>

          <div class="form-group">
            <label>Nuova password</label>
            <input
              v-model="newPsw"
              type="password"
              placeholder="••••••••"
              required
            />
          </div>

          <button type="submit" class="btn-primary" :disabled="changingPassword">
            <span v-if="!changingPassword">Cambia Password</span>
            <span v-else>Aggiornamento...</span>
          </button>
        </form>
      </div>

      <div v-else class="oauth-notice">
        <p> Accedi tramite <strong>{{ providerLabel }}</strong></p>
        <p class="notice-text">La tua password è gestita da {{ providerLabel }}. Non puoi cambiarla qui.</p>
      </div>

      <div v-if="successMessage" class="success">
         {{ successMessage }}
      </div>
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <!--  Creazione Utente admin o agente -->
      <div v-if="['ADMIN', 'AGENT', 'ROLE_ADMIN', 'ROLE_AGENT'].includes(profile.role?.toUpperCase())" class="basic-card">
        <div class="card-header">
          <h2> Crea Utente</h2>
        </div>

        <form @submit.prevent="createNewUser" class="password-form">
          <div class="form-group">
            <label>Nome</label>
            <input v-model="newUser.name" type="text" placeholder="Nome completo" required />
          </div>

          <div class="form-group">
            <label>Email</label>
            <input v-model="newUser.email" type="email" placeholder="email@example.com" required />
          </div>

          <div class="form-group">
            <label>Password</label>
            <input v-model="newUser.password" type="password" placeholder="Password sicura" required />
          </div>

           <div class="form-group">
            <label>Telefono</label>
            <input v-model="newUser.phone" type="tel" placeholder="+39 ..." required />
          </div>

          <div class="form-group" v-if="isAdmin">
            <label>Ruolo</label>
            <select v-model="newUser.role" class="role-select">
              <option value="AGENT">Agente</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          <button type="submit" class="btn-primary" :disabled="creatingUser">
            <span v-if="!creatingUser">Crea {{ isAdmin ? newUser.role : 'Agente' }}</span>
            <span v-else>Creazione...</span>
          </button>
        </form>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/authenticate'
import { getProfile, updateProfile, createAdmin, createAgent } from '@/api/auth'
import type { UserProfile, UpdateProfileRequest, PswChangeRequest, RegisterRequest } from '@/types/user'
import { useToast } from "vue-toastification"

const toast = useToast()
const auth = useAuthStore()

const creatingUser = ref(false)
const newUser = reactive({
  name: '',
  email: '',
  password: '',
  phone: '',
  role: 'AGENT'
})

// State
const profile = ref<UserProfile>({
  id: 0,
  name: '',
  email: '',
  phone: '',
  role: 'USER',
  provider: 'local'
})

const loading = ref(true)
const editMode = ref(false)
const saving = ref(false)
const changingPassword = ref(false)

const editForm = reactive({
  name: '',
  phone: ''
})

const oldPsw = ref('')
const newPsw = ref('')
const successMessage = ref('')
const errorMessage = ref('')

// Computed
const initials = computed(() => {
  if (!profile.value.name) return '👤'
  const names = profile.value.name.split(' ')
  if (names.length >= 2) {
    return (names[0][0] + names[1][0]).toUpperCase()
  }
  return profile.value.name.substring(0, 2).toUpperCase()
})

const providerLabel = computed(() => {
  const providers: Record<string, string> = {
    google: 'Google',
    facebook: 'Facebook',
    github: 'GitHub',
    local: 'Email/Password'
  }
  return providers[profile.value.provider] || 'Email/Password'
})

const providerClass = computed(() => {
  return `badge-${profile.value.provider}`
})

const isAdmin = computed(() => {
  return profile.value.role?.toUpperCase().includes('ADMIN') || auth.user?.role?.toUpperCase().includes('ADMIN')
})

const createNewUser = async () => {
    creatingUser.value = true;
    try{
        const payload: RegisterRequest = {
            name: newUser.name,
            email: newUser.email,
            password: newUser.password,
            phone: newUser.phone,
            role: newUser.role
        }

        if(isAdmin.value && newUser.role === 'ADMIN'){
             await createAdmin(payload)
             toast.success("Nuovo Admin creato con successo!")
        } else {
             await createAgent(payload)
             toast.success("Nuovo Agente creato con successo!")
        }

        // Reset form
        newUser.name = ''
        newUser.email = ''
        newUser.password = ''
        newUser.phone = ''
        newUser.role = 'AGENT'

    }catch(e){
        console.error(e)
        toast.error("Errore nella creazione dell'utente")
    }finally{
        creatingUser.value = false
    }
}

const loadProfile = async () => {
  loading.value = true
  try {
    const response = await getProfile()
    profile.value = response.data
  } catch (error) {
    console.error('Errore caricamento profilo:', error)
    errorMessage.value = 'Impossibile caricare il profilo'
  } finally {
    loading.value = false
  }
}

const enableEdit = () => {
  editMode.value = true
  editForm.name = profile.value.name
  editForm.phone = profile.value.phone
}

const cancelEdit = () => {
  editMode.value = false
  editForm.name = profile.value.name
  editForm.phone = profile.value.phone
  errorMessage.value = ''
}

const saveProfile = async () => {
  saving.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const payload: UpdateProfileRequest = {
      name: editForm.name,
      phone: editForm.phone
    }

    await updateProfile(payload)
    
    // Aggiorna profilo locale
    profile.value.name = editForm.name
    profile.value.phone = editForm.phone
    
    // Aggiorna anche lo store
    if (auth.user) {
      auth.user.name = editForm.name
      auth.user.phone = editForm.phone
      localStorage.setItem('user', JSON.stringify(auth.user))
    }

    successMessage.value = 'Profilo aggiornato con successo!'
    editMode.value = false

    // Nascondi messaggio dopo 3 secondi
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)

  } catch (error) {
    console.error('Errore aggiornamento profilo:', error)
    errorMessage.value = 'Errore durante l\'aggiornamento del profilo'
  } finally {
    saving.value = false
  }
}

const handleChangePassword = async () => {
  if (!auth.user) return

  changingPassword.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const payload: PswChangeRequest = {
      email: auth.user.email,
      oldPsw: oldPsw.value,
      newPsw: newPsw.value
    }

    const ok = await auth.updatePassword(payload)

    if (ok) {
      successMessage.value = 'Password cambiata con successo!'
      oldPsw.value = ''
      newPsw.value = ''

      setTimeout(() => {
        successMessage.value = ''
      }, 3000)
    } else {
      errorMessage.value = 'Password vecchia non corretta'
    }
  } catch (error) {
    errorMessage.value = 'Errore durante il cambio password'
  } finally {
    changingPassword.value = false
  }
}

// Lifecycle
onMounted(() => {
  loadProfile()
})
</script>

<style scoped>

.avatar-circle {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.avatar-text {
  font-size: 2.5rem;
  font-weight: 700;
  color: white;
}

.loading-state {
  text-align: center;
  padding: 3rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.badge {
  display: inline-block;
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.badge-local {
  background: #dbeafe;
  color: #1e40af;
}

.badge-google {
  background: #fee2e2;
  color: #991b1b;
}

.badge-facebook {
  background: #dbeafe;
  color: #1e40af;
}

.badge-github {
  background: #f3f4f6;
  color: #374151;
}

.role-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.4rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.oauth-notice p {
  margin: 0;
  color: #78350f;
  font-size: 1rem;
}

.oauth-notice strong {
  font-weight: 700;
}

.notice-text {
  margin-top: 0.5rem;
  font-size: 0.9rem;
  opacity: 0.8;
}

@media (max-width: 640px) {
  .profile-container {
    padding: 0 1rem;
  }

  .info-card,
  .password-card {
    padding: 1.5rem;
  }

  .edit-actions {
    flex-direction: column;
  }
}

.password-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-top: 1rem;
}
</style>
