<!-- src/components/FiltersBar.vue -->
<template>
  <form class="filters" @submit.prevent="onSearch">
    <div class="field">
      <label>Città</label>
      <input v-model="local.city" placeholder="Es. Roma" />
    </div>

    <div class="field">
      <label>Superficie min (mq)</label>
      <input v-model.number="local.minArea" type="number" min="0" />
    </div>

    <div class="field">
      <label>Prezzo max (€)</label>
      <input v-model.number="local.maxPrice" type="number" min="0" />
    </div>

    <button class="btn" type="submit">Cerca</button>
  </form>
</template>

<script setup lang="ts">
import { reactive, watchEffect } from "vue"

// ✅ Props aggiornati: accettano anche null/undefined
const props = defineProps<{
  city?: string
  minArea?: number | null
  maxPrice?: number | null
}>()

// ✅ Evento di output: anche qui permettiamo null
const emit = defineEmits<{
  (e: "search", payload: { city?: string; minArea?: number | null; maxPrice?: number | null }): void
}>()

// Stato locale (per lavorare sui valori dell’utente senza toccare subito lo store)
const local = reactive({
  city: props.city ?? "",
  minArea: props.minArea ?? null,
  maxPrice: props.maxPrice ?? null,
})

// Se cambiano le props dal genitore → aggiorniamo
watchEffect(() => {
  local.city = props.city ?? ""
  local.minArea = props.minArea ?? null
  local.maxPrice = props.maxPrice ?? null
})

// Quando l’utente clicca su "Cerca"
function onSearch() {
  emit("search", { ...local })
}
</script>

<style scoped>
.filters {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: flex-end;
}
.field {
  display: flex;
  flex-direction: column;
}
.btn {
  padding: 0.5rem 1rem;
  background: #005bbb;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
