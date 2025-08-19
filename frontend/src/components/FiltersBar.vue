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
import { reactive, watchEffect } from 'vue'

// Props iniziali (valori dal genitore)
const props = defineProps<{ city: string; minArea: number; maxPrice: number }>()
// Emettiamo un evento quando l'utente preme "Cerca"
const emit = defineEmits<{ (e: 'search', payload: { city: string; minArea: number; maxPrice: number }): void }>()

// Copia locale per non toccare subito lo store
const local = reactive({ city: props.city, minArea: props.minArea, maxPrice: props.maxPrice })

// Se cambiano le props (es. restore stato), aggiorniamo la copia
watchEffect(() => {
  local.city = props.city
  local.minArea = props.minArea
  local.maxPrice = props.maxPrice
})

function onSearch() {
  emit('search', { ...local })
}
</script>

<style scoped>
.filters { display: grid; grid-template-columns: 1fr 1fr 1fr auto; gap: 12px; margin: 16px 0; }
.field { display:flex; flex-direction:column; }
label { font-size: 12px; color:#666; margin-bottom: 6px; }
input { padding: 10px 12px; border:1px solid #ddd; border-radius: 8px; }
.btn { padding: 10px 16px; border: none; border-radius: 8px; background:#0c5db1; color:white; cursor:pointer; }
.btn:hover { opacity: 0.9; }
@media (max-width: 720px){
  .filters { grid-template-columns: 1fr; }
}
</style>