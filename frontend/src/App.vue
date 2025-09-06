<!-- src/App.vue -->



<template>
  <div class="app">
    
    <header class="header">
      <div class="container header-inner">
        <RouterLink to="/" class="logo">Dieti Estates</RouterLink>


        <!-- NAVBAR DINAMICA -->
        <nav class="nav">
          <RouterLink to="/">Annunci</RouterLink>

          <!-- Link solo per admin -->
          <RouterLink 
  v-if="auth.user && auth.user.role?.toUpperCase() === 'ADMIN'" 
  to="/admin"
>
  Admin
</RouterLink>

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
      <div class="container">© {{ new Date().getFullYear() }} Dieti Estates</div>
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
.header {
  background: #0c5db1;
  color: white;
}
.header-inner {
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding: 12px 0;
}

/* 🔹 Logo stile H1 */
.logo {
  font-size: 28px;        /* grande come un h1 */
  font-weight: 800;       /* molto bold */
  text-transform: uppercase;
  text-decoration: none;  /* senza sottolineatura */
  color: white;           /* resta bianco */
  letter-spacing: 1px;    /* leggero spazio tra lettere */
  transition: color 0.3s ease, transform 0.2s ease;
}
.logo:hover {
  color: #ffd700;         /* giallo oro al passaggio */
  transform: scale(1.05); /* leggero ingrandimento */
}

.nav a {
  color: white;
  margin-left: 16px;
  text-decoration: none;
}
.nav a.router-link-active {
  text-decoration: underline;
}
.footer {
  margin-top: 48px;
  padding: 24px 0;
  color:#666;
  border-top: 1px solid #eee;
}
.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 16px;
}
</style>
