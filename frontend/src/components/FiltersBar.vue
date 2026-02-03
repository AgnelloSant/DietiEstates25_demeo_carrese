<!-- src/components/FiltersBar.vue -->
<template>
 <form class="filters" @submit.prevent="onSearch">
  <div class="field">
    <label for="city-input">Città</label> <input id="city-input" v-model="local.city" placeholder="Es. Roma" /> </div>

  <div class="field">
    <label for="min-area-input">Superficie min (mq)</label> <input id="min-area-input" v-model.number="local.minArea" type="number" min="0" /> </div>

  <div class="field">
    <label for="max-price-input">Prezzo max (€)</label> <input id="max-price-input" v-model.number="local.maxPrice" type="number" min="0" /> </div>

  <div class="field">
    <label for="listing-type-select">Tipologia</label> <select id="listing-type-select" v-model="local.listingType"> <option value="">Tutte</option>
      <option value="vendita">Vendita</option>
      <option value="affitto">Affitto</option>
    </select>
  </div>

  <div class="field">
    <label for="rooms-input">Stanze</label> <input id="rooms-input" v-model.number="local.rooms" type="number" min="1" /> </div>

  <div class="field">
    <label for="energy-class-select">Classe Energetica</label> <select id="energy-class-select" v-model="local.energyClass"> <option value="">Tutte</option>
      <option v-for="cls in energyClasses" :key="cls" :value="cls">
        {{ cls }}
      </option>
    </select>
  </div>

  <div class="filters-actions">
    <button type="submit" class="btn-search">Cerca</button>
    <RouterLink to="/map-search" class="btn-map-search">
      Ricerca su mappa
    </RouterLink>
  </div>
</form>
</template>

<script setup lang="ts">
import { reactive, watchEffect } from "vue"

// Props dal parent (home view)
const props = defineProps<{
  city?: string
  minArea?: number | null
  maxPrice?: number | null
  listingType?: string
  rooms?: number | null
  energyClass?: string
}>()

const emit = defineEmits<{
  (e: "search", payload: {
    city?: string
    minArea?: number | null
    maxPrice?: number | null
    listingType?: string
    rooms?: number | null
    energyClass?: string
  }): void
}>()

// Stato locale del form
const local = reactive({
  city: props.city ?? "",
  minArea: props.minArea ?? null,
  maxPrice: props.maxPrice ?? null,
  listingType: props.listingType ?? "",
  rooms: props.rooms ?? null,
  energyClass: props.energyClass ?? "",
})

// Classi energetiche disponibili
const energyClasses = ["A", "B", "C", "D", "E", "F", "G"]

// Sincronizza stato con props in caso di aggiornamento esterno
watchEffect(() => {
  local.city = props.city ?? ""
  local.minArea = props.minArea ?? null
  local.maxPrice = props.maxPrice ?? null
  local.listingType = props.listingType ?? ""
  local.rooms = props.rooms ?? null
  local.energyClass = props.energyClass ?? ""
})

// Funzione submit
function onSearch() {
  emit("search", { ...local })
}
</script>

<style scoped>
.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 1rem;
  align-items: end;
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.field {
  display: flex;
  flex-direction: column;
  font-size: 0.9rem;
}
.field label {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}
input, select {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 0.9rem;
}

.filters-actions {
  display: flex;
  gap: 0.6rem;
  align-items: center;
  justify-content: flex-start;
  grid-column: span 2; /* occupa due colonne */
}

.btn-search,
.btn-map-search {
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
  border: none;
  font-size: 0.9rem;
}

.btn-search {
  background: #0c5db1;
  color: white;
}
.btn-search:hover {
  background: #094a8a;
}

.btn-map-search {
  background: #ffd700;
  color: #0c5db1;
  text-decoration: none;
  display: flex;
  align-items: center;
}
.btn-map-search:hover {
  background: #e6c200;
}
</style>
