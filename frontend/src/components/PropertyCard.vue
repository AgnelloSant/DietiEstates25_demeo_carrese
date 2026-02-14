<template>
  <div class="property-card" :class="{ compact }">
    <div v-if="property.listingType" class="listing-type" :class="property.listingType.toLowerCase()">
      {{ property.listingType.toUpperCase() }}
    </div>

    <img :src="property.imageUrl ? getContentUrl(property.imageUrl) : '/placeholder-house.jpg'" alt="Foto immobile" class="property-img"/>

    <div class="property-info">
      <h3>{{ property.title }}</h3>
      <p>{{ property.city }} • {{ property.area }} m² • {{ property.address }}</p>
      <p class="price">€ {{ property.price ? property.price.toLocaleString() : 'N/A' }}</p>

      <p v-if="property.rooms || property.energyClass" class="extras">
        <span v-if="property.rooms">🛏️ {{ property.rooms }} stanze</span>
        <span v-if="property.energyClass"> • 🔋 Classe {{ property.energyClass }}</span>
      </p>

      <div class="badges">
        <span v-if="property.nearSchool" class="badge">🏫 Scuole</span>
        <span v-if="property.nearPark" class="badge">🌳 Parchi</span>
        <span v-if="property.nearTransport" class="badge">🚌 Trasporti</span>
      </div>

      <!-- Azioni -->
      <div class="card-actions">
        <RouterLink :to="`/properties/${property.id}`" class="details-btn">Dettagli</RouterLink>
        
        <button
          class="fav-btn"
          :class="{ active: isFavourite }"
          @click="$emit('toggle-fav', property.id)"
        >
          <i class="fa-solid fa-heart"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PropertySearchDTO } from "@/types/Properties"

defineProps<{ property: PropertySearchDTO; compact?: boolean; isFavourite?: boolean }>()
defineEmits<{
  (e: "toggle-fav", id: number): void
}>()


function getContentUrl(path: string) {
    const baseUrl = import.meta.env.VITE_API_PROPERTY_URL || ''
    return `${baseUrl}/uploads/${path}`
}
</script>

<style scoped>
.property-card { 
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: transform 0.2s ease;
  display: flex;
  flex-direction: column;
}
.property-card:hover {
  transform: translateY(-4px);
}
.property-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}
.property-info {
  padding: 1rem;
}
.property-info h3 {
  font-size: 1.2rem;
  margin-bottom: 0.3rem;
  color: #0c5db1;
}
.price {
  font-weight: bold;
  color: #28a745;
  margin: 0.5rem 0;
}
.extras {
  font-size: 0.9rem;
  color: #555;
  margin-top: 4px;
}
.badges {
  margin-top: 8px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.badge {
  font-size: 0.8rem;
  background: #eaf4ff;
  color: #0c5db1;
  padding: 2px 6px;
  border-radius: 6px;
}
.card-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}
.details-btn {
  padding: 6px 12px;
  background: #0c5db1;
  color: white;
  border-radius: 6px;
  text-decoration: none;
  font-size: 0.85rem;
  transition: background 0.2s;
}
.details-btn:hover {
  background: #094a88;
}
.fav-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.3rem;
  color: #aaa;
  transition: color 0.2s;
}
.fav-btn.active {
  color: #e63946; /* rosso acceso quando attivo */
}

/* Badge tipo annuncio */
.listing-type {
  position: absolute;
  margin: 10px;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
}
.listing-type.vendita {
  background: #28a745;
}
.listing-type.affitto {
  background: #ff9800;
}
</style>
