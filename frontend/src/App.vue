<!-- src/App.vue -->
<template>
  <div class="max-content-container">
    <header class="header-modern">
      <div class="header-glow"></div>
      <div class="container header-inner">
        <RouterLink to="/" class="logo-modern">
          <span class="logo-icon">🏠</span>
          <span class="logo-text">
            <span class="logo-primary">Dieti</span>
            <span class="logo-secondary">Estates</span>
          </span>
        </RouterLink>

        <!-- NAVBAR DINAMICA -->
        <nav class="nav-modern">
          <RouterLink to="/" class="nav-link">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
              <polyline points="9 22 9 12 15 12 15 22"></polyline>
            </svg>
            Annunci
          </RouterLink>
<!-- Pagina modifica annunci (solo amministratori)-->
          <RouterLink
            v-if="auth.user && auth.user.role?.toUpperCase() === 'ADMIN'"
            to="/admin"
            class="nav-link nav-link-admin"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            </svg>
            Admin
          </RouterLink>

          <!-- Insights (Agenti e Admin) -->
          <RouterLink
            v-if="auth.user && (auth.user.role?.toUpperCase().includes('AGENT') || auth.user.role?.toUpperCase().includes('ADMIN'))"
            to="/analitics"
            class="nav-link"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="20" x2="18" y2="10"></line>
              <line x1="12" y1="20" x2="12" y2="4"></line>
              <line x1="6" y1="20" x2="6" y2="14"></line>
            </svg>
            Insights
          </RouterLink>

          <!--  Bottone Pubblica Annuncio -->
          <button class="btn-publish-modern" @click="handlePublishClick">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            Pubblica
          </button>

          

          <!-- Profiloe logout (solo utenti loggati)-->
          <template v-if="auth.user">
            <RouterLink to="/profile" class="nav-link">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              Profilo
            </RouterLink>
            
            
            <a href="#" @click.prevent="showLogoutConfirm = true" class="nav-link nav-link-logout">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                <polyline points="16 17 21 12 16 7"></polyline>
                <line x1="21" y1="12" x2="9" y2="12"></line>
              </svg>
              Logout
            </a>
          </template>



          <!-- Link per guest -->
          <template v-else>
            <RouterLink to="/login" class="nav-link nav-link-login">
              Login
            </RouterLink>
            <RouterLink to="/register" class="nav-link nav-link-register">
              Registrati
            </RouterLink>
          </template>
        </nav>
      </div>
    </header>

    <!-- Contenuto della pagina corrente -->
    <main class="page-container">
      <RouterView />
    </main>

    <footer class="footer-modern">
      <div class="footer-content">
        <div class="footer-brand">
          <div class="footer-logo">
            <span class="logo-icon">🏠</span>
            <span class="footer-brand-text">Dieti Estates</span>
          </div>
          <p class="footer-tagline">La tua casa dei sogni ti aspetta</p>
        </div>
        
        <div class="footer-links">
          <div class="footer-section">
            <h4>Navigazione</h4>
            <RouterLink to="/">Home</RouterLink>
            <RouterLink to="/publish">Pubblica</RouterLink>
          </div>
          
          
        </div>
      </div>
      
      <div class="footer-bottom">
        <div class="container">
          © {{ new Date().getFullYear() }} Dieti Estates - Tutti i diritti riservati
        </div>
      </div>
    </footer>

    <!--  Popup Logout -->
    <Transition name="modal-fade">
      <div v-if="showLogoutConfirm" class="modal-overlay-modern" @click="showLogoutConfirm = false">
        <div class="modal-modern modal-modern--small" @click.stop>
          <div class="modal-icon modal-icon--warning">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
          </div>
          
          <h3 class="modal-title">Conferma Logout</h3>
          <p class="modal-message">Sei sicuro di voler uscire?</p>
          
          <div class="modal-actions-modern">
            <button @click="confirmLogout" class="btn-modal btn-modal-danger">
              Sì, esci
            </button>
            <button @click="showLogoutConfirm = false" class="btn-modal btn-modal-secondary">
              Annulla
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <Transition name="modal-fade">
      <div v-if="showPublishPrompt" class="modal-overlay-modern" @click="showPublishPrompt = false">
        <div class="modal-modern modal-modern--small" @click.stop>
          <div class="modal-icon modal-icon--warning">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
          </div>
          
          <h3 class="modal-title">Non sei autorizzato</h3>
          <p class="modal-message">Per pubblicare un annuncio devi lavorare presso un agenzia immobiliare.</p>
          
          <div v-if="!auth.user" class="modal-actions-modern">
            <RouterLink to="/login" class="btn-modal btn-modal-primary" @click="showPublishPrompt = false">
              Accedi
            </RouterLink>
            <RouterLink to="/register" class="btn-modal btn-modal-secondary" @click="showPublishPrompt = false">
              Registrati
            </RouterLink>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Popup Pubblica -->
    <Transition name="modal-fade">
      <div v-if="showLoginPrompt" class="modal-overlay-modern" @click="showLoginPrompt = false">
        <div class="modal-modern" @click.stop>
          <div class="modal-icon modal-icon--info">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="16" x2="12" y2="12"></line>
              <line x1="12" y1="8" x2="12.01" y2="8"></line>
            </svg>
          </div>
          
          <h3 class="modal-title">Accesso Richiesto</h3>
          <p class="modal-message">Per pubblicare un annuncio devi prima effettuare l'accesso o registrarti.</p>
          
          <div class="modal-actions-modern">
            <RouterLink to="/login" class="btn-modal btn-modal-primary" @click="showLoginPrompt = false">
              Accedi
            </RouterLink>
            <RouterLink to="/register" class="btn-modal btn-modal-secondary" @click="showLoginPrompt = false">
              Registrati
            </RouterLink>
          </div>
        </div>
      </div>
    </Transition>
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
const showPublishPrompt = ref(false);

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
  } else if (auth.user.role?.toUpperCase() === "USER") {
    //show popup: non puoi pubblicare annunci
    showPublishPrompt.value = true;
  } else {
    router.push("/publish");
  }
};


</script>

<style scoped>
/*  Menu si impila su mobile

Footer diventa single column

 Modal si adatta allo schermo */
/* ============================================
   🎨 HEADER MODERNO
   ============================================ */

.header-modern {
  position: relative;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  color: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.header-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb, #667eea);
  background-size: 200% 100%;
  animation: glow-shift 3s ease infinite;
}

@keyframes glow-shift {
  0%, 100% { background-position: 0% 0%; }
  50% { background-position: 100% 0%; }
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 0;
}

/* Logo moderno */
.logo-modern {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-decoration: none;
  transition: transform 0.3s ease;
}

.logo-modern:hover {
  transform: translateY(-2px);
}

.logo-icon {
  font-size: 2rem;
  filter: drop-shadow(0 0 10px rgba(255, 215, 0, 0.5));
  animation: float-logo 3s ease-in-out infinite;
}

@keyframes float-logo {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.logo-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.logo-primary {
  font-size: 1.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #ffffff, #cbd5e1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-secondary {
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
  text-transform: uppercase;
  letter-spacing: 2px;
}

/* Nav moderna */
.nav-modern {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1rem;
  text-decoration: none;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 600;
  font-size: 0.95rem;
  border-radius: 10px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-link svg {
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.nav-link:hover {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

.nav-link:hover svg {
  opacity: 1;
}

.nav-link.router-link-active {
  color: white;
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
}

/* Admin link */
.nav-link-admin {
  background: rgba(255, 215, 0, 0.1);
  color: #ffd700;
  border: 1px solid rgba(255, 215, 0, 0.3);
}

.nav-link-admin:hover {
  background: rgba(255, 215, 0, 0.2);
  border-color: rgba(255, 215, 0, 0.5);
}

/* Logout link */
.nav-link-logout {
  color: rgba(239, 68, 68, 0.9);
}

.nav-link-logout:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

/* Login/Register */
.nav-link-login,
.nav-link-register {
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.nav-link-register {
  background: rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.4);
}

/* Pulsante Pubblica moderno */
.btn-publish-modern {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  padding: 0.7rem 1.5rem;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 700;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
}

.btn-publish-modern::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #764ba2, #667eea);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.btn-publish-modern:hover::before {
  opacity: 1;
}

.btn-publish-modern:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.btn-publish-modern svg {
  position: relative;
  z-index: 1;
}

/* ============================================
    FOOTER MODERNO
   ============================================ */

.footer-modern {
  margin-top: 4rem;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  color: rgba(255, 255, 255, 0.8);
  padding: 3rem 0 0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem 2rem;
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 3rem;
}

.footer-brand {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.footer-brand-text {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #ffffff, #cbd5e1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-tagline {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.95rem;
  margin: 0;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
}

.footer-section h4 {
  color: white;
  font-size: 0.9rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 1rem;
}

.footer-section a {
  display: block;
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  margin-bottom: 0.75rem;
  transition: color 0.3s ease;
}

.footer-section a:hover {
  color: white;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: 1.5rem 0;
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.9rem;
}

/* ============================================
    MODAL MODERNO
   ============================================ */

.modal-overlay-modern {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  padding: 1rem;
}

.modal-modern {
  background: white;
  border-radius: 20px;
  padding: 2.5rem;
  max-width: 450px;
  width: 100%;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modal-scale-in 0.3s ease-out;
}

.modal-modern--small {
  max-width: 380px;
}

@keyframes modal-scale-in {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.modal-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-icon--warning {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  box-shadow: 0 10px 30px rgba(251, 191, 36, 0.3);
}

.modal-icon--info {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
}

.modal-icon svg {
  color: white;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 0.75rem;
}

.modal-message {
  color: #64748b;
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.modal-actions-modern {
  display: flex;
  gap: 0.75rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-modal {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
}

.btn-modal-danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
}

.btn-modal-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.4);
}

.btn-modal-primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.btn-modal-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-modal-secondary {
  background: #f1f5f9;
  color: #475569;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-modal-secondary:hover {
  background: #e2e8f0;
  transform: translateY(-2px);
}

/* Transition modal */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* ============================================
   📱 RESPONSIVE
   ============================================ */

@media (max-width: 768px) {
  .header-inner {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem 0;
  }

  .nav-modern {
    flex-wrap: wrap;
    justify-content: center;
  }

  .nav-link {
    font-size: 0.85rem;
    padding: 0.5rem 0.75rem;
  }

  .btn-publish-modern {
    width: 100%;
    justify-content: center;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .footer-links {
    grid-template-columns: 1fr;
  }

  .modal-modern {
    padding: 2rem;
  }
}
</style>
