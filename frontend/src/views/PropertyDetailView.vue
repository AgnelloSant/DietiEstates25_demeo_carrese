<template>
  <div v-if="property" class="detail-container">
    <!-- 🏠 Immagine -->
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

    <!-- ✅ Vantaggi zona -->
    <div class="advantages" v-if="property.nearSchool || property.nearPark || property.nearTransport">
      <h3>Vantaggi della zona</h3>
      <ul>
        <li v-if="property.nearSchool">🏫 Vicino a scuole</li>
        <li v-if="property.nearPark">🌳 Vicino a parchi</li>
        <li v-if="property.nearTransport">🚌 Vicino a trasporti pubblici</li>
      </ul>
    </div>

    <!-- 🌍 Mappa -->
    <div v-if="property.latitude && property.longitude" id="map" style="height: 400px; border-radius: 12px; margin-top: 1.5rem;"></div>
    <p v-else class="detail-coords">📍 Coordinate non disponibili</p>
  </div>

  <!-- stato caricamento -->
  <div v-else class="loading">Caricamento...</div>
</template>

<script setup lang="ts">
import { onMounted, ref, nextTick } from 'vue'
import { httpProperty } from '@/api/http'
import type { PropertyDetailDTO } from '@/types/Properties'

// 🗺️ Leaflet
import L from "leaflet"
import "leaflet/dist/leaflet.css"

// ✅ Fix icone Leaflet (CDN)
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png",
  iconUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png",
  shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png",
});

// id dalla route
const props = defineProps<{ id: string }>()
const property = ref<PropertyDetailDTO | null>(null)

onMounted(async () => {
  try {
    const { data } = await httpProperty.get<PropertyDetailDTO>(`/properties/${props.id}`)
    property.value = data

    // aspetta che Vue aggiorni il DOM
    if (property.value?.latitude && property.value?.longitude) {
      await nextTick()

      const map = L.map("map").setView([property.value.latitude, property.value.longitude], 14)

      L.tileLayer(`https://maps.geoapify.com/v1/tile/osm-bright/{z}/{x}/{y}.png?apiKey=c4dc78950f8f486cbf36cb126f4efda1`, {
        attribution: "© OpenMapTiles © OpenStreetMap contributors",
        maxZoom: 20
      }).addTo(map)

      L.marker([property.value.latitude, property.value.longitude]).addTo(map)
        .bindPopup(`<b>${property.value.title}</b><br>${property.value.city}<br>€ ${property.value.price.toLocaleString()}`)
    }
  } catch (err) {
    console.error("Errore caricamento proprietà", err)
  }
})

</script>

<style scoped>
.detail-container {
  max-width: 1000px;
  margin: 2rem auto;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background: #fff;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  font-family: "Inter", sans-serif;
}

.detail-image img {
  width: 100%;
  height: 420px;
  object-fit: cover;
  border-radius: 12px;
}

.detail-info {
  padding: 1rem 0;
  border-bottom: 1px solid #eee;
}

.detail-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.3rem;
  color: #0c5db1;
}

.detail-city {
  font-size: 1.1rem;
  color: #555;
  margin-bottom: 0.5rem;
}

.detail-area,
.detail-price {
  font-size: 1.2rem;
  margin: 0.3rem 0;
}

.detail-price {
  font-weight: bold;
  color: #28a745;
}

.detail-description {
  font-size: 1rem;
  line-height: 1.6;
  margin: 1rem 0;
  color: #333;
}

.detail-date {
  font-size: 0.9rem;
  color: #777;
}

.advantages {
  margin-top: 20px;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.advantages h3 {
  margin-bottom: 10px;
  color: #0c5db1;
}

.loading {
  text-align: center;
  margin-top: 3rem;
  font-size: 1.2rem;
}

.detail-coords {
  font-size: 0.95rem;
  color: #666;
  margin-top: 8px;
}
</style>
