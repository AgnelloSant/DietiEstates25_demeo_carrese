<template>
  <div class="profile-container">
    <!-- Header con Avatar -->
    <div class="profile-header">
      <div class="avatar-circle">
        <span class="avatar-text">{{ initials }}</span>
      </div>
      <h1>Il tuo Profilo</h1>
      <p class="subtitle">Gestisci le tue informazioni personali</p>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Caricamento...</p>
    </div>

    <!-- Profilo caricato -->
    <div v-else class="profile-content">
      
      <!-- Card Info Utente -->
      <div class="info-card">
        <div class="card-header">
          <h2>📋 Informazioni Personali</h2>
          <button 
            v-if="!editMode" 
            @click="enableEdit" 
            class="btn-edit"
          >
            ✏️ Modifica
          </button>
        </div>

        <div class="info-grid">
          <!-- Nome -->
          <div class="info-item">
            <label>👤 Nome</label>
            <input 
              v-if="editMode" 
              v-model="editForm.name" 
              type="text" 
              placeholder="Inserisci il tuo nome"
              class="edit-input"
            />
            <p v-else class="info-value">{{ profile.name || 'Non specificato' }}</p>
          </div>

          <!-- Email (non editabile) -->
          <div class="info-item">
            <label>📧 Email</label>
            <p class="info-value">{{ profile.email }}</p>
            <span class="badge" :class="providerClass">{{ providerLabel }}</span>
          </div>

          <!-- Telefono -->
          <div class="info-item">
            <label>📞 Telefono</label>
            <input 
              v-if="editMode" 
              v-model="editForm.phone" 
              type="tel" 
              placeholder="+39 123 456 7890"
              class="edit-input"
            />
            <p v-else class="info-value">{{ profile.phone || 'Non specificato' }}</p>
          </div>

          <!-- Ruolo (non editabile) -->
          <div class="info-item">
            <label>🔑 Ruolo</label>
            <p class="info-value">
              <span class="role-badge">{{ profile.role }}</span>
            </p>
          </div>
        </div>

        <!-- Pulsanti edit mode -->
        <div v-if="editMode" class="edit-actions">
          <button @click="saveProfile" class="btn-save" :disabled="saving">
            <span v-if="!saving">Salva</span>
            <span v-else>⏳ Salvataggio...</span>
          </button>
          <button @click="cancelEdit" class="btn-cancel">❌Annulla</button>
        </div>
      </div>

      <!-- Card Cambio Password (solo per utenti local) -->
      <div v-if="profile.provider === 'local'" class="password-card">
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
            <span v-if="!changingPassword">🔑 Cambia Password</span>
            <span v-else>⏳ Aggiornamento...</span>
          </button>
        </form>
      </div>

      <!-- Messaggio per utenti OAuth -->
      <div v-else class="oauth-notice">
        <p> Accedi tramite <strong>{{ providerLabel }}</strong></p>
        <p class="notice-text">La tua password è gestita da {{ providerLabel }}. Non puoi cambiarla qui.</p>
      </div>

      <!-- Messaggi feedback -->
      <div v-if="successMessage" class="alert alert-success">
        ✅ {{ successMessage }}
      </div>
      <div v-if="errorMessage" class="alert alert-error">
        ⚠️ {{ errorMessage }}
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/authenticate'
import { getProfile, updateProfile } from '@/api/auth'
import type { UserProfile, UpdateProfileRequest, PswChangeRequest } from '@/types/user'

const auth = useAuthStore()

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

// Methods
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
.profile-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 1.5rem;
}

.profile-header {
  text-align: center;
  margin-bottom: 3rem;
}

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

.profile-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.5rem;
}

.subtitle {
  color: #64748b;
  font-size: 1rem;
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

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.info-card,
.password-card {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f1f5f9;
}

.card-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.btn-edit {
  background: #f1f5f9;
  color: #475569;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-edit:hover {
  background: #e2e8f0;
  transform: translateY(-2px);
}

.info-grid {
  display: grid;
  gap: 1.5rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-item label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #64748b;
}

.info-value {
  font-size: 1.1rem;
  color: #1e293b;
  margin: 0;
}

.edit-input {
  width: 100%;
  padding: 0.9rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.edit-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.badge {
  display: inline-block;
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  margin-top: 0.5rem;
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

.edit-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 2px solid #f1f5f9;
}

.btn-save,
.btn-cancel {
  flex: 1;
  padding: 0.9rem;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-save {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-save:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-cancel {
  background: #f1f5f9;
  color: #475569;
}

.btn-cancel:hover {
  background: #e2e8f0;
}

.password-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #64748b;
}

.form-group input {
  padding: 0.9rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.btn-primary {
  padding: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.oauth-notice {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
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

.alert {
  padding: 1rem 1.5rem;
  border-radius: 12px;
  font-weight: 600;
}

.alert-success {
  background: #d1fae5;
  color: #065f46;
  border: 2px solid #10b981;
}

.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 2px solid #ef4444;
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
</style>
