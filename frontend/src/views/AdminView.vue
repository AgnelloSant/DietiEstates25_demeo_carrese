<template>
  <div class="admin-container">
    <h2 class="title">Gestione Proprietà</h2>

    <!-- ✍️ Form creazione nuova proprietà -->
    <form @submit.prevent="create" class="property-form">
      <div class="form-group">
        <input v-model="form.title" placeholder="🏠 Titolo dell'annuncio (Es. Appartamento luminoso)" required />
      </div>

      <div class="form-group">
        <input v-model="form.city" placeholder="📍 Città (Es. Roma)" required />
      </div>

      <div class="form-group">
        <input v-model.number="form.area" type="number" min="0" placeholder="📏 Superficie (mq)" required />
      </div>

      <div class="form-group">
        <input v-model.number="form.price" type="number" min="0" placeholder="💰 Prezzo (€)" required />
      </div>

      <!-- 🆕 Campi aggiuntivi per posizione -->
      <div class="form-group">
       <input v-model.number="form.latitude" type="number" step="0.0001" placeholder="Latitudine" />
      </div>

      <div class="form-group">
      <input v-model.number="form.longitude" type="number" step="0.0001" placeholder="Longitudine" />
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
          📍 {{ p.city }} <br />
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

// ✅ Iniettiamo lo store Pinia che gestisce le proprietà
const store = usePropertyStore()
const { list } = storeToRefs(store)

// Stato errori locali
const error = ref("")

// DTO per creare nuova proprietà (con lat/lng)
const form = reactive<PropertyCreateDTO>({
  title: "",
  city: "",
  area: 0,
  price: 0,
  latitude: null,
  longitude: null
})

// ➕ Crea nuova proprietà
async function create() {
  try {
    await store.addProperty(form)

    // reset form
    form.title = ""
    form.city = ""
    form.area = 0
    form.price = 0
    form.latitude = 0
    form.longitude = 0

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
.admin-container {
  max-width: 900px;
  margin: 2rem auto;
  padding: 2rem;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  font-family: "Inter", sans-serif;
}

.title {
  font-size: 2rem;
  font-weight: 700;
  color: #0c5db1;
  margin-bottom: 1rem;
  text-align: center;
}

.subtitle {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
  margin: 1.5rem 0 0.5rem;
}

.property-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.form-group input {
  width: 100%;
  padding: 0.7rem 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.2s;
}

.form-group input:focus {
  border-color: #0c5db1;
  outline: none;
  box-shadow: 0 0 5px rgba(12, 93, 177, 0.3);
}

.btn-create {
  grid-column: span 2;
  padding: 0.8rem;
  background: #0c5db1;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: background 0.3s ease;
}

.btn-create:hover {
  background: #094a88;
}

.property-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.property-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 10px;
  margin-bottom: 0.8rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.property-info {
  font-size: 0.95rem;
  line-height: 1.4;
}

.btn-delete {
  background: #dc3545;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.3s ease;
}

.btn-delete:hover {
  background: #a71d2a;
}

.error {
  color: red;
  margin-top: 1rem;
  text-align: center;
}
</style>
