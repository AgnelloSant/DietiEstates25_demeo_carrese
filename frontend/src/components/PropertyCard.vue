<template>
  <div class="property-card">
    <!-- immagine -->
    <img
      class="property-img"
      :src="property.imageUrl || '/placeholder-house.jpg'"
      alt="Immobile"
    />

    <button class="fav-btn" :title="`Aggiungi ai preferiti`" @click.stop="onFavClick">
        <!-- semplice cuore SVG -->
        <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
          <path
            d="M12 21s-6.716-4.317-9.173-7.053C1.01 11.977 1 9.61 2.343 8.05 3.686 6.49 6.08 6.21 7.76 7.54L12 11l4.24-3.46c1.68-1.33 4.074-1.05 5.417.51 1.343 1.56 1.333 3.927.173 5.897C18.716 16.683 12 21 12 21z"
            fill="currentColor"
          />
        </svg>
      </button>

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
const props = defineProps<{ property: PropertySearchDTO }>()

const emit = defineEmits<{
  (e: "add-fav", propId: number): void
}>()

function onFavClick() {
  emit("add-fav", props.property.id)
}
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
