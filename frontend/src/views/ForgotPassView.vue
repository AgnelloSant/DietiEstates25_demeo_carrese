<!-- src/views/ForgotPasswordView.vue -->
<template>
  <div class="auth-container">
    <h1>Password dimenticata</h1>
    <form @submit.prevent="handleReset" class="auth-form">
      <div class="form-group">
        <label>Email</label>
        <input v-model="email" type="email" required />
      </div>
      <button type="submit" class="btn-primary">Invia reset</button>
      <p v-if="message" class="success-message">{{ message }}</p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"

const email = ref("")
const message = ref("")

// placeholder — da implementare con API backend
const handleReset = async () => {
  message.value = `Se esiste un account con ${email.value}, riceverai istruzioni via email.`
  email.value = ""
}
</script>
