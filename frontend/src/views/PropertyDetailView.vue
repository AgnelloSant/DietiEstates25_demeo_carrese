<template>
  <div v-if="property" class="detail-container">
    <!-- 🏠 Immagine grande -->
    <div class="detail-image">
      <img
        :src="property.imageUrl || '/placeholder-house.jpg'"
        alt="Foto immobile"
      />
    </div>

    <!-- 📋 Info principali -->
    <div class="detail-info">
      <h1 class="detail-title">{{ property.title }}</h1>
      <p class="detail-city"><i class="fa-solid fa-location-dot"></i> {{ property.city }}</p>
      <p class="detail-area"><strong>{{ property.area }} m²</strong></p>
      <p class="detail-price">€ {{ property.price.toLocaleString() }}</p>

      <!-- descrizione -->
      <p v-if="property.description" class="detail-description">
        {{ property.description }}
      </p>

      <!-- data pubblicazione -->
      <p v-if="property.publishedAt" class="detail-date">
        Pubblicato il {{ new Date(property.publishedAt).toLocaleDateString() }}
      </p>
    </div>
  </div>

  <!-- stato caricamento -->
  <div v-else class="loading">Caricamento...</div>
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

<style scoped>
.detail-container {
  max-width: 1000px;
  margin: 2rem auto;
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
  background: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.detail-image img {
  width: 100%;
  height: 400px;
  object-fit: cover; /* ritaglia mantenendo proporzioni */
  border-radius: 12px;
}

.detail-info {
  padding: 1rem 0;
}

.detail-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 0.3rem;
  color: #0c5db1; /* blu stile immobiliare.it */
}

.detail-city {
  font-size: 1.1rem;
  color: #555;
  margin-bottom: 0.5rem;
}

.detail-area {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
}

.detail-price {
  font-size: 1.8rem;
  font-weight: bold;
  color: #28a745; /* verde prezzo */
  margin: 1rem 0;
}

.detail-description {
  font-size: 1rem;
  line-height: 1.5;
  margin: 1rem 0;
  color: #333;
}

.detail-date {
  font-size: 0.9rem;
  color: #777;
}

.loading {
  text-align: center;
  margin-top: 3rem;
  font-size: 1.2rem;
}
</style>
