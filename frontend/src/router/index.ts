// src/router/index.ts
import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/authenticate";

const HomeView = () => import("@/views/HomeView.vue");
const PropertyDetailView = () => import("@/views/PropertyDetailView.vue");
const AdminView = () => import("@/views/AdminView.vue");
const LoginView = () => import("@/views/LoginView.vue");
const RegisterView = () => import("@/views/RegisterView.vue");
const ProfileView = () => import("@/views/ProfileView.vue");

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", name: "home", component: HomeView },
    { path: "/property/:id", name: "property-detail", component: PropertyDetailView, props: true },
    { path: "/login", name: "login", component: LoginView },
    { path: "/register", name: "register", component: RegisterView },
    { path: "/profile", name: "profile", component: ProfileView, meta: { requiresAuth: true } },
    { path: "/admin", name: "admin", component: AdminView, meta: { requiresAdmin: true } },
  ],
});

// Guard globale: controlla accesso a rotte protette
router.beforeEach((to, from, next) => {
  const auth = useAuthStore();

  if (to.meta.requiresAuth && !auth.user) {
    // Non loggato → rimanda al login
    return next({ name: "login" });
  }

  if (to.meta.requiresAdmin && auth.user?.role?.toUpperCase() !== "ADMIN") {
  return next({ name: "home" });
}


  next();
});
