<!-- src/components/PropertyCard.vue -->
<template>
  <!-- 1) l'intera card è cliccabile: @click richiama goToDetail() -->
  <article class="card" @click="goToDetail">
    <!-- 2) thumb fittizia: qui potresti mettere un'immagine vera -->
    <div class="thumb" />

    <!-- 3) corpo della card con testi -->
    <div class="body">
      <!-- 4) titolo dell'annuncio -->
      <h3 class="title">{{ item.title }}</h3>
      <!-- 5) meta info: città + superficie -->
      <p class="meta">{{ item.city }} · {{ item.area }} mq</p>
      <!-- 6) prezzo formattato in euro italiani -->
      <div class="price">€ {{ formatPrice(item.price) }}</div>
      <!-- 7) data pubblicazione in formato locale italiano -->
      <small class="date">Pubblicato: {{ formatDate(item.publishedAt) }}</small>
    </div>
  </article>
</template>

<script setup lang="ts">
// 8) importiamo il router per navigare al dettaglio
import { useRouter } from 'vue-router'
// 9) import del tipo del DTO per tipizzare la prop
import type { PropertySearchDTO } from '@/types/Properties'

// 🔸 Prop: la card riceve un singolo annuncio
const props = defineProps<{ item: PropertySearchDTO }>()

// 🔟 istanza router per i redirect
const router = useRouter()

// 🔹 utility per il prezzo in formato it-IT (1.234.567)
function formatPrice(n: number) {
  return new Intl.NumberFormat('it-IT').format(n)
}

// 🔹 utility per la data locale
function formatDate(iso: string) {
  const d = new Date(iso)
  return d.toLocaleDateString('it-IT')
}

// 🔹 naviga alla rotta di dettaglio passando l'id come param
function goToDetail() {
  router.push({ name: 'property-detail', params: { id: props.item.id } })
}
</script>

<style scoped>
/* stile contenitore card */
.card { display:flex; gap:16px; padding:12px; border:1px solid #eee; border-radius:12px; cursor:pointer; }
.card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
/* miniatura placeholder */
.thumb { width:160px; height:120px; background:#f2f2f2; border-radius:8px; }
/* contenuto testuale */
.body { display:flex; flex-direction:column; gap:6px; }
.title { margin:0; font-size:18px; font-weight:700; }
.meta { color:#666; }
.price { font-size:18px; font-weight:700; color:#0c5db1; }
.date { color:#888; }
/* responsività */
@media (max-width: 720px){
  .card { flex-direction:column; }
  .thumb { width:100%; height:180px; }
}
</style>

