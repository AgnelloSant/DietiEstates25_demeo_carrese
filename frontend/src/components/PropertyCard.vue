<template>
  <div
    class="property-card"
    :class="{ compact }"
    @click="goToDetail"
  >
    <div
      v-if="property.listingType"
      class="listing-type"
      :class="property.listingType.toLowerCase()"
    >
      {{ property.listingType.toUpperCase() }}
    </div>

    <img
      :src="property.imageUrl ? getContentUrl(property.imageUrl) : '/placeholder-house.jpg'"
      alt="Foto immobile"
      class="property-img"
    />

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

      <div class="card-actions">
        <span class="open-label">Apri dettagli</span>

        <button
          class="fav-btn"
          :class="{ active: isFavourite }"
          @click.stop="emitToggleFav"
        >
          <i class="fa-solid fa-heart"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router"
import type { PropertySearchDTO } from "@/types/Properties"

const router = useRouter()

const props = defineProps<{
  property: PropertySearchDTO
  compact?: boolean
  isFavourite?: boolean
}>()

const emit = defineEmits<{
  (e: "toggle-fav", id: number): void
}>()

function getContentUrl(path: string) {
  const baseUrl = import.meta.env.VITE_API_PROPERTY_URL || ""
  return `${baseUrl}/uploads/${path}`
}

function goToDetail() {
  router.push(`/properties/${props.property.id}`)
}

function emitToggleFav() {
  emit("toggle-fav", props.property.id)
}
</script>

<style scoped>
.property-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
  position: relative;
  cursor: pointer;
}

.property-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
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
  margin-top: 12px;
}

.open-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #0c5db1;
  opacity: 0.85;
}

.fav-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.3rem;
  color: #aaa;
  transition: color 0.2s ease, transform 0.2s ease;
  padding: 0.2rem;
  z-index: 2;
}

.fav-btn:hover {
  transform: scale(1.08);
}

.fav-btn.active {
  color: #e63946;
}

/* Badge tipo annuncio */
.listing-type {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
  z-index: 1;
}

.listing-type.vendita {
  background: #28a745;
}

.listing-type.affitto {
  background: #ff9800;
}
</style>