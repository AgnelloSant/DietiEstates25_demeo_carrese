<template>
  <div class="home">
    <h1>Annunci immobiliari</h1>

    <!-- 🔍 Filtri -->
    <FiltersBar
      :city="city"
      :minArea="minArea"
      :maxPrice="maxPrice"
      @search="search"
    />

    <!-- ⭐ Preferiti -->
    <section class="favourites">
      <h3>
        Annunci preferiti
        <small v-if="isLoggedIn && !store.favLoading && !store.favError">
          ({{ store.favList.length }})
        </small>
      </h3>

      <div v-if="!isLoggedIn">
        <p>Effettua l'accesso per visualizzare gli annunci salvati.</p>
      </div>

      <div v-else>
        <p v-if="store.favLoading">Caricamento preferiti…</p>
        <p v-else-if="store.favError" class="error">{{ store.favError }}</p>

        <div v-else-if="store.favList.length" class="fav-row">
          <PropertyCard
            v-for="p in store.favList"
            :key="`fav-${p.id}`"
            :property="p"
            class="fav-card"
          />
        </div>

        <p v-else>Nessun preferito al momento.</p>

        <!-- (opz) azioni -->
        <div class="fav-actions">
          <button class="btn" @click="refreshFavs">Aggiorna</button>
        </div>
      </div>
    </section>

    <h3>Annunci in evidenza</h3>

    <!-- ⏳ Stato caricamento -->
    <p v-if="store.loading">Caricamento…</p>

    <!-- ⚠️ Errore -->
    <p v-if="store.error" class="error">{{ store.error }}</p>

    <!-- 📋 Lista immobili -->
    <div v-if="store.list.length" class="property-list">
      <PropertyCard
        v-for="p in store.list"
        :key="p.id"
        :property="p"
        @add-fav="store.addToFavourites"
      />
    </div>

    <!-- 🛑 Nessun immobile -->
    <p v-else-if="!store.loading">Nessun immobile trovato.</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { usePropertyStore } from "@/stores/properties"
import FiltersBar from "@/components/FiltersBar.vue"
import PropertyCard from "@/components/PropertyCard.vue"

// TODO: sostituire con stato reale auth
const isLoggedIn = true

const store = usePropertyStore()

// Filtri
const city = ref("")
const minArea = ref<number | null>(null)
const maxPrice = ref<number | null>(null)

function search(payload: { city?: string; minArea?: number | null; maxPrice?: number | null }) {
  store.fetchList(payload)
}

function refreshFavs() {
  store.fetchFavList()
}

// Caricamento iniziale
onMounted(() => {
  store.fetchList()
  if (isLoggedIn) {
    store.fetchFavList()
  }
})
</script>

<style scoped>
.home { max-width: 1100px; margin: auto; padding: 1rem; }

.favourites { margin: 1.25rem 0 2rem; }
.fav-row {
  display: grid;
  grid-auto-flow: column;
  gap: 12px;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  padding-bottom: 8px;
}
.fav-card { min-width: 260px; scroll-snap-align: start; }

.property-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}
@media (max-width: 640px){
  .property-list{ grid-template-columns:1fr; }
}

.error { color: #d00; }
.btn {
  padding: .5rem .75rem; border: 1px solid #ddd; border-radius: .5rem; background: #fff; cursor: pointer;
}
.btn:hover { background: #f7f7f7; }
.fav-actions { margin-top: .5rem; color: #d00;}
</style>
