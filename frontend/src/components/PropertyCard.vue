<template>
  <div class="property-card">
    <!-- immagine -->
    <img
      class="property-img"
      :src="property.imageUrl || '/placeholder-house.jpg'"
      alt="Immobile"
    />

    <!-- contenuto -->
    <div class="property-content">
      <h3 class="property-price">€ {{ property.price.toLocaleString() }}</h3>
      <h2 class="property-title">{{ property.title }}</h2>
      <p class="property-info">
        {{ property.city }} • {{ property.area }} mq
      </p>
      <RouterLink :to="`/property/${property.id}`" class="details-btn">
        Vedi dettagli
      </RouterLink>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PropertySearchDTO } from "@/types/Properties"
defineProps<{ property: PropertySearchDTO }>()
</script>

<style scoped>
.property-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  transition: transform 0.2s ease;
  background: #fff;
}
.property-card:hover {
  transform: translateY(-3px);
}

.property-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.property-content {
  padding: 1rem;
}

.property-price {
  color: #0c5db1;
  font-size: 1.3rem;
  margin: 0 0 0.5rem 0;
  font-weight: bold;
}

.property-title {
  font-size: 1.1rem;
  margin: 0 0 0.25rem 0;
}

.property-info {
  color: #666;
  margin: 0 0 0.5rem 0;
}

.property-date {
  font-size: 0.85rem;
  color: #999;
  margin-bottom: 0.75rem;
}

.details-btn {
  display: inline-block;
  padding: 0.5rem 1rem;
  background: #0c5db1;
  color: #fff;
  border-radius: 4px;
  text-decoration: none;
  font-size: 0.9rem;
}
.details-btn:hover {
  background: #084080;
}
</style>
