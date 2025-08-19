

<!-- src/App.vue -->
<template>
  <div class="app">
    <!-- Header stile immobiliare.it -->
    <header class="header">
      <div class="container header-inner">
        <h1 class="logo">Dieties Estates</h1>

        <!-- NAVBAR DINAMICA -->
        <nav class="nav">
          <RouterLink to="/">Annunci</RouterLink>

          <!-- Link solo per admin -->
          <RouterLink v-if="auth.user?.role === 'ADMIN'" to="/admin">Admin</RouterLink>

          <!-- Link per utenti loggati -->
          <template v-if="auth.user">
            <RouterLink to="/profile">Profilo</RouterLink>
            <a href="#" @click.prevent="logout">Logout</a>
          </template>

          <!-- Link per guest -->
          <template v-else>
            <RouterLink to="/login">Login</RouterLink>
            <RouterLink to="/register">Registrati</RouterLink>
          </template>
        </nav>
      </div>
    </header>

    <!-- Contenuto della pagina corrente -->
    <main class="container">
      <RouterView />
    </main>

    <footer class="footer">
      <div class="container">© {{ new Date().getFullYear() }} Dieties Estates</div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from "@/stores/authenticate";
import { useRouter } from "vue-router";

const auth = useAuthStore();
const router = useRouter();

const logout = () => {
  auth.logout();
  router.push("/");
};
</script>

<style scoped>
.header { background: #0c5db1; color: white; }
.header-inner { display:flex; align-items:center; justify-content:space-between; padding: 12px 0; }
.logo { font-size: 20px; font-weight: 700; }
.nav a { color: white; margin-left: 16px; text-decoration: none; }
.nav a.router-link-active { text-decoration: underline; }
.footer { margin-top: 48px; padding: 24px 0; color:#666; border-top: 1px solid #eee; }
.container { max-width: 1100px; margin: 0 auto; padding: 0 16px; }
</style>
