<template>
  <div class="profile-container">
    <h1>Profilo</h1>

    <!-- 👤 Info utente -->
    <div v-if="auth.user" class="profile-card">
      <p><b>👤 Nome:</b> {{ auth.user.name }}</p>
      <p><b>📧 Email:</b> {{ auth.user.email }}</p>
      <p><b>📞 Telefono:</b> {{ auth.user.phone }}</p>
      <p><b>🔑 Ruolo:</b> {{ auth.user.role }}</p>
    </div>

    <!-- 🔑 Cambio password -->
    <div class="password-card">
      <h2>Cambia password</h2>
      <form @submit.prevent="handleChangePassword" class="password-form">
        <div class="form-group">
          <input
            v-model="oldPsw"
            type="password"
            placeholder="Vecchia password"
            required
          />
        </div>
        <div class="form-group">
          <input
            v-model="newPsw"
            type="password"
            placeholder="Nuova password"
            required
          />
        </div>
        <button type="submit" class="btn-primary">Aggiorna</button>
      </form>

      <!-- messaggi -->
      <p v-if="message" class="success">{{ message }}</p>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuthStore } from "@/stores/authenticate";
import type { PswChangeRequest } from "@/types/user";

const auth = useAuthStore();
const oldPsw = ref("");
const newPsw = ref("");
const message = ref("");
const error = ref("");

const handleChangePassword = async () => {
  if (!auth.user) {
    error.value = "⚠️ Devi essere loggato";
    return;
  }
  const payload: PswChangeRequest = {
    email: auth.user.email,
    oldPsw: oldPsw.value,
    newPsw: newPsw.value,
  };
  const ok = await auth.updatePassword(payload);
  if (ok) {
    message.value = "✅ Password cambiata con successo";
    oldPsw.value = newPsw.value = "";
    error.value = "";
  } else {
    error.value = "❌ Errore durante il cambio password";
    message.value = "";
  }
};
</script>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  font-family: "Inter", sans-serif;
}

.profile-card,
.password-card {
  margin-bottom: 2rem;
  padding: 1.5rem;
  border-radius: 12px;
  background: #f9f9f9;
}

.profile-card p {
  font-size: 1rem;
  margin: 0.5rem 0;
}

.password-card h2 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: #0c5db1;
}

.password-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.password-form input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
}

.btn-primary {
  background: #0c5db1;
  color: white;
  border: none;
  padding: 0.75rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary:hover {
  background: #09498c;
}

.success {
  color: #28a745;
  margin-top: 1rem;
}

.error {
  color: #d00;
  margin-top: 1rem;
}
</style>
