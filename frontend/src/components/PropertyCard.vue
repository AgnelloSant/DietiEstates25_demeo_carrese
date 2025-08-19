<template>
  <!-- Card cliccabile che porta al dettaglio -->
  <article class="card" @click="goToDetail">
    <!-- Thumbnail fittizia -->
    <div class="thumb" />

    <!-- Corpo della card -->
    <div class="body">
      <h3 class="title">{{ item.title }}</h3>
      <p class="meta">{{ item.city }} · {{ item.area }} mq</p>
      <div class="price">€ {{ formatPrice(item.price) }}</div>
      <!-- ❌ Tolta la data perché non esiste in PropertySearchDTO -->
    </div>
  </article>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
// DTO corretto: PropertySearchDTO ora include id
import type { PropertySearchDTO } from '@/types/Properties'

// Prop: la card riceve un singolo annuncio
const props = defineProps<{ item: PropertySearchDTO }>()

// Router per navigare al dettaglio
const router = useRouter()

// Utility per formattare il prezzo
function formatPrice(n: number) {
  return new Intl.NumberFormat('it-IT').format(n)
}

// Naviga alla rotta di dettaglio con l'id
function goToDetail() {
  router.push({ name: 'property-detail', params: { id: props.item.id } })
}
</script>

<style scoped>
.card { display:flex; gap:16px; padding:12px; border:1px solid #eee; border-radius:12px; cursor:pointer; }
.card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
.thumb { width:160px; height:120px; background:#f2f2f2; border-radius:8px; }
.body { display:flex; flex-direction:column; gap:6px; }
.title { margin:0; font-size:18px; font-weight:700; }
.meta { color:#666; }
.price { font-size:18px; font-weight:700; color:#0c5db1; }
.date { color:#888; }
@media (max-width: 720px){
  .card { flex-direction:column; }
  .thumb { width:100%; height:180px; }
}
</style>
