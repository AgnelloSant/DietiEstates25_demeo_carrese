<template>
  <div class="auth-container">
    <h1>Accedi</h1>
    <form @submit.prevent="handleLogin" class="auth-form">
      <!-- 📧 Email -->
      <div class="form-group">
        <label>Email</label>
        <input v-model="email" type="email" placeholder="Inserisci la tua email" required />
      </div>

      <!-- 🔑 Password -->
      <div class="form-group">
        <label>Password</label>
        <input v-model="password" type="password" placeholder="Inserisci la tua password" required />
      </div>

      <!-- ▶️ Pulsante -->
      <button type="submit" class="btn-primary">Login</button>

      <!-- ❌ Errori -->
      <p v-if="error" class="error-message">{{ error }}</p>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authenticate";

const router = useRouter();
const auth = useAuthStore();

const email = ref("");
const password = ref("");
const error = ref("");

// submit login
const handleLogin = async () => {
  const success = await auth.loginUser({ email: email.value, password: password.value });
  if (success) {
    router.push("/");
  } else {
    error.value = "Email o password non validi";
  }
};
</script>
