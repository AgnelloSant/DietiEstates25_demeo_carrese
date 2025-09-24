<template>
  <div class="admin-container">
    <h2 class="title">Gestione Proprietà</h2>

    <!-- ✍️ Form creazione nuova proprietà -->
    <form @submit.prevent="create" class="property-form">
      <!-- Titolo -->
      <div class="form-group">
        <input v-model="form.title" placeholder="🏠 Titolo annuncio" required />
      </div>

      <!-- Città -->
      <div class="form-group">
        <input v-model="form.city" placeholder="📍 Città (Es. Roma)" required />
      </div>

      <!-- Indirizzo completo (via + civico) -->
      <div class="form-group">
        <input
          v-model="form.address"
          placeholder="📍 Indirizzo (via e civico)"
          required
        />
      </div>

      <!-- Superficie -->
      <div class="form-group">
        <input
          v-model.number="form.area"
          type="number"
          min="0"
          placeholder="📏 Superficie (mq)"
          required
        />
      </div>

      <!-- Prezzo -->
      <div class="form-group">
        <input
          v-model.number="form.price"
          type="number"
          min="0"
          placeholder="💰 Prezzo (€)"
          required
        />
      </div>

      <!-- Tipo inserzione -->
      <div class="form-group">
        <select v-model="form.listingType" required>
          <option disabled value="">-- Tipo annuncio --</option>
          <option value="vendita">Vendita</option>
          <option value="affitto">Affitto</option>
        </select>
      </div>

      <!-- Numero stanze -->
      <div class="form-group">
        <input
          v-model.number="form.rooms"
          type="number"
          min="1"
          placeholder="🛏️ Numero stanze"
          required
        />
      </div>

      <!-- Classe energetica -->
      <div class="form-group">
        <select v-model="form.energyClass" required>
          <option disabled value="">-- Classe energetica --</option>
          <option>A</option>
          <option>B</option>
          <option>C</option>
          <option>D</option>
          <option>E</option>
          <option>F</option>
          <option>G</option>
        </select>
      </div>

      <!-- Lat/Long (opzionali, solo debug) -->
      <div class="form-group">
        <input
          v-model.number="form.latitude"
          type="number"
          step="0.0001"
          placeholder="Latitudine (opzionale)"
        />
      </div>
      <div class="form-group">
        <input
          v-model.number="form.longitude"
          type="number"
          step="0.0001"
          placeholder="Longitudine (opzionale)"
        />
      </div>

      <button type="submit" class="btn-create">➕ Crea Annuncio</button>
    </form>

    <hr />

    <!-- 📋 Lista proprietà già presenti -->
    <h3 class="subtitle">Lista Annunci</h3>
    <ul class="property-list">
      <li v-for="p in list" :key="p.id" class="property-item">
        <div class="property-info">
          <strong>{{ p.title }}</strong><br />
          📍 {{ p.city }}  <br />
          📏 {{ p.area }} mq — 💰 €{{ p.price.toLocaleString("it-IT") }}
        </div>
        <!-- 🗑️ Pulsante elimina -->
        <button class="btn-delete" @click="remove(p.id)">Elimina</button>
      </li>
    </ul>

    <!-- ⚠️ Messaggi di stato -->
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="list.length === 0">Nessuna proprietà presente.</p>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from "vue"
import { storeToRefs } from "pinia"
import { usePropertyStore } from "@/stores/properties"
import type { PropertyCreateDTO } from "@/types/Properties"

// ✅ Iniettiamo lo store Pinia
const store = usePropertyStore()
const { list } = storeToRefs(store)

// Stato errori locali
const error = ref("")

// DTO per creare nuova proprietà
const form = reactive({
  title: "",
  description: "",
  city: "",
  address: "",
  area: 0,
  price: 0,
  latitude: null as number | null,
  longitude: null as number | null,
  listingType: "vendita" as "vendita" | "affitto",
  rooms: 1,
  energyClass: "A",
  idUser: 0,    // ✅ NUOVO: ID utente
  views: 0      // ✅ NUOVO: Visualizzazioni (sempre 0 alla creazione)
})

// ➕ Crea nuova proprietà
async function create() {
  try {
    await store.addProperty(form)

    // reset form
    form.title = ""
    form.city = ""
    form.address = ""
    form.area = 0
    form.price = 0
    form.latitude = null
    form.longitude = null
    form.listingType = "vendita"
    form.rooms = 1
    form.energyClass = "A"

    error.value = ""
  } catch (err) {
    error.value = "Errore nella creazione della proprietà"
    console.error(err)
  }
}

// ❌ Elimina proprietà
async function remove(id: number) {
  try {
    await store.removeProperty(id)
    error.value = ""
  } catch (err) {
    error.value = "Errore durante l'eliminazione"
    console.error(err)
  }
}

// 🔄 Al caricamento mostro subito tutte le proprietà
onMounted(() => {
  store.fetchList()
})
</script>

<style scoped>
/* stessi stili che avevi */
</style>
