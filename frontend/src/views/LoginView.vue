<template>
  <div class="auth-container">
    <h1>Accedi</h1>
    <!-- form login -->
    <form @submit.prevent="handleLogin" class="auth-form">
      <div class="form-group">
        <label>Email</label>
        <input v-model="form.email" type="email" required />
      </div>
      <div class="form-group">
        <label>Password</label>
        <input v-model="form.password" type="password" required />
      </div>
      <button type="submit" class="btn-primary">Accedi</button>
      <p v-if="error" class="error-message">{{ error }}</p>
      <div class="auth-links">
        <RouterLink to="/register">Registrati</RouterLink>
        <RouterLink to="/forgot-password">Password dimenticata?</RouterLink>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
// reactive = oggetto reattivo, ref = variabile reattiva singola
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/authenticate";
import type { LoginRequest } from "@/types/user";

// store + router
const auth = useAuthStore();
const router = useRouter();

// DTO per login
const form = reactive<LoginRequest>({ email: "", password: "" });
const error = ref("");

// funzione submit
const handleLogin = async () => {
  const success = await auth.loginUser(form);
  if (success) {
    // se ADMIN → admin page
    if (auth.user?.role === "ADMIN") router.push("/admin");
    else router.push("/"); 
  } else {
    error.value = "Email o password errati";
  }
};
</script>
 