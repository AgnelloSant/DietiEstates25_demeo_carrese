<template>
  <div v-if="property" class="detail">
    <h2>{{ property.title }}</h2>
    <p><strong>Città:</strong> {{ property.city }}</p>
    <p><strong>Superficie:</strong> {{ property.area }} m²</p>
    <p><strong>Prezzo:</strong> € {{ property.price.toLocaleString() }}</p>
    <p v-if="property.description"><strong>Descrizione:</strong> {{ property.description }}</p>
    <p v-if="property.publishedAt">
      <small>Pubblicato il {{ new Date(property.publishedAt).toLocaleDateString() }}</small>
    </p>
  </div>
  <div v-else>Caricamento...</div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { http } from '@/api/http'
import type { PropertyDetailDTO } from '@/types/Properties'

// id dalla route
const props = defineProps<{ id: string }>()
const property = ref<PropertyDetailDTO | null>(null)

onMounted(async () => {
  try {
    const { data } = await http.get<PropertyDetailDTO>(`/properties/${props.id}`)
    property.value = data
  } catch (err) {
    console.error("Errore caricamento proprietà", err)
  }
})
</script>
