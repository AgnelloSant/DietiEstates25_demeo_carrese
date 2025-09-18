<template>
  <div class="auth-container">
    <h1 class="auth-title">Registrati</h1>

    <!-- 📝 Form registrazione -->
    <form @submit.prevent="handleRegister" class="auth-form">
      <div class="form-group">
        <label>Nome</label>
        <input v-model="form.name" type="text" required />
      </div>

      <div class="form-group">
        <label>Email</label>
        <input v-model="form.email" type="email" required />
      </div>

      <div class="form-group">
        <label>Password</label>
        <input v-model="form.password" type="password" required />
      </div>

      <div class="form-group">
        <label>Telefono</label>
        <input v-model="form.phone" type="tel" />
      </div>

      <!-- bottone -->
      <button type="submit" class="btn-primary">Registrati</button>

      <!-- messaggi errore -->
      <p v-if="error" class="error-message">{{ error }}</p>
    </form>

    <!-- link a login -->
    <p class="redirect">
      Hai già un account?
      <RouterLink to="/login">Accedi</RouterLink>
    </p>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { useAuthStore } from "@/stores/authenticate"
import type { RegisterRequest } from "@/types/user"

const router = useRouter()
const auth = useAuthStore()

// DTO coerente con backend
const form = reactive<RegisterRequest>({
  name: "",
  email: "",
  password: "",
  phone: "",
  role: "USER", // default
})

const error = ref("")

// submit registrazione
const handleRegister = async () => {
  try {
    const success = await auth.registerUser(form)
    if (success) {
      router.push("/login")
    } else {
      error.value = "Errore durante la registrazione"
    }
  } catch (e: any) {
    console.error("Errore registrazione", e)
    error.value = "Registrazione fallita"
  }
}
</script>
