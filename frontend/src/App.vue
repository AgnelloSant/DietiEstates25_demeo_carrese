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

          <!--  Bottone Pubblica Annuncio -->
          <button class="btn-publish" @click="handlePublishClick">
            + Pubblica
          </button>

          <!-- Link per utenti loggati -->
          <template v-if="auth.user">
            <RouterLink to="/profile">Profilo</RouterLink>
            <!-- Prima mostra il popup, poi fa logout -->
            <a href="#" @click.prevent="showLogoutConfirm = true">Logout</a>
            <RouterLink to="/analitics">Insights</RouterLink>
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

    <!--  Popup Logout -->
    <div v-if="showLogoutConfirm" class="modal-overlay">
      <div class="modal">
        <h3>Sei sicuro di voler uscire?</h3>
        <div class="modal-actions">
          <!--: Chiama la funzione di conferma -->
          <button @click="confirmLogout" class="btn-danger">Sì</button>
          <button @click="showLogoutConfirm = false" class="btn-secondary">No</button>
        </div>
      </div>
    </div>

    <!-- Popup Pubblica -->
    <div v-if="showLoginPrompt" class="modal-overlay">
      <div class="modal">
        <h3>Vuoi pubblicare un annuncio?</h3>
        <p>Effettua prima l'accesso o registrati.</p>
        <div class="modal-actions">
          <RouterLink to="/login" class="btn-primary" @click="showLoginPrompt = false">
            Login
          </RouterLink>
          <RouterLink to="/register" class="btn-secondary" @click="showLoginPrompt = false">
            Registrati
          </RouterLink>
          <button @click="showLoginPrompt = false" class="btn-secondary">Annulla</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuthStore } from "@/stores/authenticate";
import { useRouter } from "vue-router";

const auth = useAuthStore();
const router = useRouter();

//  Variabili reattive per i popup
const showLogoutConfirm = ref(false);
const showLoginPrompt = ref(false);

//  : Funzione che effettua il logout confermato
const confirmLogout = () => {
  auth.logout();
  showLogoutConfirm.value = false;
  router.push("/");
};

// Click su "Pubblica"
const handlePublishClick = () => {
  if (!auth.user) {
    showLoginPrompt.value = true;
  } else {
    router.push("/publish");
  }
};
</script>

<style scoped>
.header {
  background: #0c5db1;
  color: white;
}
.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

/* 🔹 Logo stile H1 */
.logo {
  font-size: 28px;
  font-weight: 800;
  text-transform: uppercase;
  text-decoration: none;
  color: white;
  letter-spacing: 1px;
  transition: color 0.3s ease, transform 0.2s ease;
}
.logo:hover {
  color: #ffd700;
  transform: scale(1.05);
}

.nav {
  display: flex;
  align-items: center;
}
.nav a,
.btn-publish {
  margin-left: 16px;
  text-decoration: none;
  color: white;
}
.nav a.router-link-active {
  text-decoration: underline;
}

/* 🔹 Pulsante Pubblica */
.btn-publish {
  background: #ffd700;
  color: #0c5db1;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.3s;
}
.btn-publish:hover {
  background: #e6c200;
}

.footer {
  margin-top: 48px;
  padding: 24px 0;
  color: #666;
  border-top: 1px solid #eee;
}
.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 16px;
}

/*  Stili Modal del popup - Z-index ottimizzato */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000; /*  Aumentato per essere sopra tutto */
}

.modal {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  max-width: 320px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.modal-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  justify-content: center;
  margin-top: 1rem;
}

.btn-danger {
  background: #d9534f;
  color: #fff;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.btn-danger:hover {
  background: #c9302c;
}

.btn-secondary {
  background: #f1f1f1;
  color: #333;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  text-decoration: none; /*  Per i RouterLink */
}
.btn-secondary:hover {
  background: #ddd;
}

.btn-primary {
  background: #0c5db1;
  color: #fff;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  text-decoration: none; /* Per i RouterLink */
}
.btn-primary:hover {
  background: #094a88;
}
</style>
