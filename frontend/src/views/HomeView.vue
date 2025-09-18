<template>
  <div class="home">
    <!-- 🌟 Hero -->
    <header class="hero">
      <h1 class="hero-title">Annunci immobiliari</h1>
      <p class="hero-subtitle">Trova la casa dei tuoi sogni con un click</p>
    </header>

    <!-- 🔍 Filtri -->
    <div class="filters-wrapper">
      <FiltersBar
        :city="city"
        :minArea="minArea"
        :maxPrice="maxPrice"
        :listingType="listingType"
        :rooms="rooms"
        :energyClass="energyClass"
        @search="search"
      />
    </div>

    <!-- ⭐ Preferiti -->
    <section class="favourites">
      <h2>
        ⭐ Annunci preferiti
        <small v-if="isLoggedIn && !store.favLoading && !store.favError">
          ({{ store.favList.length }})
        </small>
      </h2>

      <div v-if="!isLoggedIn || store.favError === '401'" class="empty">
        <p>Effettua l'accesso per visualizzare gli annunci salvati.</p>
      </div>

      <div v-else>
        <p v-if="store.favLoading">Caricamento preferiti…</p>
        <p v-else-if="store.favError" class="error">{{ store.favError }}</p>

        <!-- carosello orizzontale -->
        <div v-else-if="store.favList.length" class="fav-row">
          <PropertyCard
            v-for="p in store.favList"
            :key="`fav-${p.id}`"
            :property="p"
            :compact="true"
            class="fav-card"
          />
        </div>

        <p v-else class="empty">Nessun preferito al momento.</p>

        <!-- 🔄 Azioni -->
        <div class="fav-actions">
          <button class="btn-refresh" @click="refreshFavs">🔄 Aggiorna</button>
        </div>
      </div>
    </section>

    <!-- 🏡 Annunci in evidenza -->
    <section class="featured">
      <h2>🏡 Annunci in evidenza</h2>

      <!-- ⏳ Stato caricamento -->
      <p v-if="store.loading">Caricamento…</p>
      <p v-if="store.error" class="error">{{ store.error }}</p>

      <!-- 📋 Lista immobili -->
      <div v-if="store.list.length" class="property-list">
        <PropertyCard
          v-for="p in store.list"
          :key="p.id"
          :property="p"
          @add-fav="store.addToFavourites"
        />
      </div>

      <!-- 🛑 Nessun immobile -->
      <p v-else-if="!store.loading" class="empty">Nessun immobile trovato.</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { usePropertyStore } from "@/stores/properties"
import { useAuthStore } from "@/stores/authenticate"
import FiltersBar from "@/components/FiltersBar.vue"
import PropertyCard from "@/components/PropertyCard.vue"

// 🔐 Auth
const auth = useAuthStore()
const isLoggedIn = auth.isLoggedIn

// 🏠 Property store
const store = usePropertyStore()

// 🔍 Filtri avanzati
const city = ref("")
const minArea = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const listingType = ref<string>("")   // vendita / affitto
const rooms = ref<number | null>(null)
const energyClass = ref<string>("")

// funzione per ricerca
function search(payload: {
  city?: string
  minArea?: number | null
  maxPrice?: number | null
  listingType?: string
  rooms?: number | null
  energyClass?: string
}) {
  store.fetchList(payload)
}

// aggiorna preferiti
function refreshFavs() {
  store.fetchFavList()
}

// 🚀 Caricamento iniziale
onMounted(() => {
  store.fetchList()
  if (isLoggedIn) {
    store.fetchFavList()
  }
})
</script>

<style scoped>
/* --- Hero --- */
.hero {
  text-align: center;
  padding: 2.5rem 1rem;
  background: linear-gradient(135deg, #0c5db1, #4ea8de);
  color: white;
  border-radius: 16px;
  margin-bottom: 2rem;
}
.hero-title {
  font-size: 2.4rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}
.hero-subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
}

/* --- Filtri --- */
.filters-wrapper {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 1rem;
  margin-bottom: 2rem;
}

/* --- Sezioni --- */
.favourites,
.featured {
  margin: 2rem 0;
}
.favourites h2,
.featured h2 {
  font-size: 1.6rem;
  margin-bottom: 1rem;
  color: #0c5db1;
}

/* --- Preferiti row --- */
.fav-row {
  display: grid;
  grid-auto-flow: column;
  gap: 16px;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  padding-bottom: 8px;
}
.fav-card {
  min-width: 260px;
  scroll-snap-align: start;
}

/* --- Lista proprietà --- */
.property-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

/* --- Buttons --- */
.btn-refresh {
  margin-top: 0.8rem;
  padding: 0.6rem 1.2rem;
  background: #0c5db1;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}
.btn-refresh:hover {
  background: #094a8a;
}

/* --- Stati --- */
.error {
  color: #d00;
  font-weight: 500;
}
.empty {
  color: #666;
  font-style: italic;
}
</style>
