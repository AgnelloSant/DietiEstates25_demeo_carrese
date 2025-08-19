<!-- src/views/HomeView.vue -->
<template>
  <div class="home">
    <h1>Annunci immobiliari</h1>

    <!-- 🔍 Barra di ricerca -->
    <form @submit.prevent="search" class="search-bar">
      <input v-model="city" placeholder="Città" />
      <input v-model.number="minArea" type="number" placeholder="Superficie minima (mq)" />
      <input v-model.number="maxPrice" type="number" placeholder="Prezzo massimo (€)" />
      <button type="submit">Cerca</button>
    </form>

    <!-- ⏳ Loading -->
    <p v-if="store.loading">Caricamento in corso...</p>

    <!-- ⚠️ Errore -->
    <p v-if="store.error" class="error">{{ store.error }}</p>

    <!-- 📋 Lista annunci -->
    <div v-if="store.list.length > 0" class="property-list">
      <div v-for="p in store.list" :key="p.id" class="property-card">
        <h2>{{ p.title }}</h2>
        <p><strong>Città:</strong> {{ p.city }}</p>
        <p><strong>Superficie:</strong> {{ p.area }} mq</p>
        <p><strong>Prezzo:</strong> € {{ p.price.toLocaleString() }}</p>
        <p><em>Pubblicato il: {{ new Date(p.publishedAt).toLocaleDateString() }}</em></p>
        <RouterLink :to="`/property/${p.id}`">Dettagli</RouterLink>
      </div>
    </div>

    <!-- Nessun annuncio trovato -->
    <p v-else-if="!store.loading">Nessun immobile trovato.</p>
  </div>
</template>

<script setup lang="ts">
import { usePropertyStore } from "@/stores/properties"
import { ref, onMounted } from "vue"

const store = usePropertyStore()

// Campi ricerca
const city = ref("")
const minArea = ref<number | null>(null)
const maxPrice = ref<number | null>(null)

// Funzione ricerca
const search = () => {
  store.fetchList({
    city: city.value || undefined,
    minArea: minArea.value || undefined,
    maxPrice: maxPrice.value || undefined,
  })
}

// Carica subito al montaggio
onMounted(() => {
  store.fetchList()
})
</script>

<style scoped>
.home { max-width: 1100px; margin: auto; padding: 1rem; }
.search-bar { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
.property-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}
.property-card {
  border: 1px solid #ddd;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}
</style>
