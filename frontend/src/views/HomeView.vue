<template>
  <div class="home">
    <h1>Annunci immobiliari</h1>

    <!-- 🔍 Barra filtri: component riutilizzabile -->
    <FiltersBar
      :city="city"
      :minArea="minArea ?? 0"
      :maxPrice="maxPrice ?? 0"
      @search="search"
    />

    <!-- ⏳ Stato caricamento -->
    <p v-if="store.loading">Caricamento...</p>

    <!-- ⚠️ Messaggio di errore -->
    <p v-if="store.error" class="error">{{ store.error }}</p>

    <!-- 📋 Lista immobili -->
    <div v-if="store.list.length" class="property-list">
      <!-- Uso PropertyCard per ogni immobile -->
      <PropertyCard
  v-for="p in store.list"
  :key="p.id"
  :property="p"
/>

    </div>

    <!-- 🛑 Nessun immobile trovato -->
    <p v-else-if="!store.loading">Nessun immobile trovato.</p>
  </div>
</template>

<script setup lang="ts">
/*
 * Importiamo lo store (Pinia) e i componenti UI
 */
import { usePropertyStore } from "@/stores/properties"
import { ref, onMounted } from "vue"
import FiltersBar from "@/components/FiltersBar.vue"
import PropertyCard from "@/components/PropertyCard.vue"

// Istanza dello store
const store = usePropertyStore()

// Filtri ricerca (variabili collegate a FiltersBar)
const city = ref("")
const minArea = ref<number | null>(null)
const maxPrice = ref<number | null>(null)

/*
 * Funzione di ricerca chiamata dall'evento "search"
 * emesso da FiltersBar
 */
function search(payload: { city: string; minArea: number; maxPrice: number }) {
  store.fetchList(payload)
}

// Carica subito gli annunci al mount
onMounted(() => store.fetchList())
</script>

<style scoped>
.home { max-width: 1100px; margin: auto; padding: 1rem; }
.property-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}
.error { color: red; }
</style>
