<!-- src/components/PropertyCard.vue -->
<template>
  <div class="property-card">
    <img :src="property.imageUrl || '/placeholder-house.jpg'" alt="Foto immobile" class="property-img"/>

    <div class="property-info">
      <h3>{{ property.title }}</h3>
      <p>{{ property.city }} • {{ property.area }} m²</p>
      <p class="price">€ {{ property.price.toLocaleString() }}</p>

      <!-- 🆕 Badge vantaggi -->
      <div class="badges">
        <span v-if="property.nearSchool" class="badge">🏫 Scuole</span>
        <span v-if="property.nearPark" class="badge">🌳 Parchi</span>
        <span v-if="property.nearTransport" class="badge">🚌 Trasporti</span>
      </div>

      <RouterLink :to="`/properties/${property.id}`" class="details-btn">Dettagli</RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PropertySearchDTO } from "@/types/Properties"

defineProps<{ property: PropertySearchDTO }>()
</script>

<style scoped>
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
</style>
