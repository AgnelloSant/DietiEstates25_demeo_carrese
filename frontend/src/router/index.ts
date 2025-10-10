// src/router/index.ts
import { createRouter, createWebHistory } from "vue-router"
import { useAuthStore } from "@/stores/authenticate"

// Lazy loading dei componenti (carica solo quando servono)
const HomeView = () => import("@/views/HomeView.vue")
const PropertyDetailView = () => import("@/views/PropertyDetailView.vue")
const AdminView = () => import("@/views/AdminView.vue")
const LoginView = () => import("@/views/LoginView.vue")
const RegisterView = () => import("@/views/RegisterView.vue")
const ProfileView = () => import("@/views/ProfileView.vue")
const PublishView = () => import("@/views/PublishView.vue")
const MapSearchView = () => import("@/views/MapSearchView.vue")
const AnaliticsView = () => import("@/views/AnaliticsView.vue")
const OAuth2Callback = () => import("@/views/OAuth2Callback.vue")  // 
export const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ========== PAGINE PUBBLICHE ==========
    { 
      path: "/", 
      name: "home", 
      component: HomeView 
    },
    { 
      path: "/properties/:id", 
      name: "property-detail", 
      component: PropertyDetailView, 
      props: true  // Passa :id come prop al componente
    },
    { 
      path: "/login", 
      name: "login", 
      component: LoginView 
    },
    { 
      path: "/register", 
      name: "register", 
      component: RegisterView 
    },
    { 
      path: "/map-search", 
      name: "map-search",
      component: MapSearchView 
    },
    
    // ========== OAUTH2 CALLBACK ==========
    // Questa route è chiamata dal backend dopo login Google/Facebook
    { 
      path: "/oauth2/callback",
      name: "oauth2-callback", 
      component: OAuth2Callback
    },

    // ========== PAGINE AUTENTICATE ==========
    { 
      path: "/profile", 
      name: "profile", 
      component: ProfileView, 
      meta: { requiresAuth: true }  // Solo utenti loggati
    },
    { 
      path: "/publish", 
      name: "publish", 
      component: PublishView, 
      meta: { requiresAuth: true }
    },
    { 
      path: "/analitics", 
      name: "analitics", 
      component: AnaliticsView,
      meta: { requiresAuth: true }  // Aggiungi se serve auth
    },

    // ========== PAGINE ADMIN ==========
    { 
      path: "/admin", 
      name: "admin", 
      component: AdminView, 
      meta: { requiresAdmin: true }  // Solo admin
    },
  ],
})

// 🔐 Navigation Guard: controlla accesso a rotte protette
router.beforeEach((to, from, next) => {
  const auth = useAuthStore()

  // Se la route richiede autenticazione e l'utente non è loggato
  if (to.meta.requiresAuth && !auth.user) {
    console.log("🔒 Accesso negato: utente non autenticato")
    return next({ name: "login" })
  }

  // Se la route richiede ruolo admin e l'utente non è admin
  if (to.meta.requiresAdmin && auth.user?.role?.toUpperCase() !== "ADMIN") {
    console.log("🔒 Accesso negato: solo admin")
    return next({ name: "home" })
  }

  // Tutto ok, prosegui
  next()
})
