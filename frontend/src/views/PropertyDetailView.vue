<!-- Mostra informazioni complete su una proprietà selezionata. -->

<template>
  <div v-if="property" class="detail">
    <h2>{{ property.title }}</h2>
    <p><strong>Città:</strong> {{ property.city }}</p>
    <p><strong>Superficie:</strong> {{ property.area }} m²</p>
    <p><strong>Prezzo:</strong> € {{ property.price.toLocaleString() }}</p>
    <p><small>Pubblicato il {{ new Date(property.publishedAt).toLocaleDateString() }}</small></p>
  </div>
  <div v-else>Caricamento...</div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { http } from '@/api/http'
import type { PropertySearchDTO } from '@/types/Properties'

// Recuperiamo l'ID dalla route
const props = defineProps<{ id: string }>()
const property = ref<PropertySearchDTO | null>(null)

onMounted(async () => {
  const { data } = await http.get<PropertySearchDTO>(`/properties/${props.id}`)
  property.value = data
})
</script>

<style scoped>
.detail { padding: 16px; }
.detail h2 { margin-bottom: 12px; }
</style>