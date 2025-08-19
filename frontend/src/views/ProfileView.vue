<template>
  <div class="profile-container">
    <h1>Profilo</h1>
    <!-- info utente -->
    <div v-if="auth.user">
      <p><b>Nome:</b> {{ auth.user.name }}</p>
      <p><b>Email:</b> {{ auth.user.email }}</p>
      <p><b>Telefono:</b> {{ auth.user.phone }}</p>
      <p><b>Ruolo:</b> {{ auth.user.role }}</p>
    </div>

    <!-- cambio password -->
    <form @submit.prevent="handleChangePassword">
      <label>Vecchia password</label>
      <input v-model="oldPsw" type="password" required />
      <label>Nuova password</label>
      <input v-model="newPsw" type="password" required />
      <button type="submit">Aggiorna</button>
    </form>
    <p v-if="message" class="success">{{ message }}</p>
    <p v-if="error" class="error">{{ error }}</p>
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
    error.value = "Devi essere loggato";
    return;
  }
  // costruiamo DTO
  const payload: PswChangeRequest = {
    email: auth.user.email,
    oldPsw: oldPsw.value,
    newPsw: newPsw.value,
  };
  const ok = await auth.updatePassword(payload);
  if (ok) {
    message.value = "Password cambiata con successo";
    oldPsw.value = newPsw.value = "";
  } else {
    error.value = "Errore durante il cambio password";
  }
};
</script>
