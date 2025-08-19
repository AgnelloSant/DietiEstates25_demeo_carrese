<template>
  <div class="admin-container">
    <h2>Gestione Proprietà</h2>

    <!-- ✍️ Form creazione nuova proprietà -->
    <form @submit.prevent="create" class="property-form">
      <input v-model="form.title" placeholder="Titolo" required />
      <input v-model="form.city" placeholder="Città" required />
      <input v-model.number="form.area" type="number" placeholder="Superficie (mq)" required />
      <input v-model.number="form.price" type="number" placeholder="Prezzo (€)" required />
      <button type="submit">Crea</button>
    </form>

    <hr />

    <!-- 📋 Lista proprietà già presenti -->
    <ul class="property-list">
      <li v-for="p in list" :key="p.title" class="property-item">
        <strong>{{ p.title }}</strong> — {{ p.city }} — {{ p.area }}mq — €{{ p.price.toLocaleString("it-IT") }}
        <!-- 🗑️ Pulsante elimina -->
        <button @click="remove(p.id)">Elimina</button>
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

// ✅ Iniettiamo lo store
const store = usePropertyStore()
// storeToRefs per rendere reattiva la lista
const { list } = storeToRefs(store)

// Stato errori locali
const error = ref("")

// DTO per creare nuova proprietà
const form = reactive<PropertyCreateDTO>({
  title: "",
  city: "",
  area: 0,
  price: 0,
})

// Crea nuova proprietà
async function create() {
  try {
    await store.addProperty(form)  // chiamata allo store → API POST
    // reset form
    form.title = ""
    form.city = ""
    form.area = 0
    form.price = 0
    error.value = ""
  } catch (err) {
    error.value = "Errore nella creazione della proprietà"
    console.error(err)
  }
}

// Elimina proprietà
async function remove(id: number) {
  try {
    await store.removeProperty(id)
    error.value = ""
  } catch (err) {
    error.value = "Errore durante l'eliminazione"
    console.error(err)
  }
}

// Al caricamento mostro subito tutte le proprietà
onMounted(() => {
  store.fetchList()
})
</script>

<style scoped>
.admin-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 2rem;
  border-radius: 8px;
  background: #f8f9fa;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}
.property-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}
.property-form input { flex: 1 1 180px; padding: 0.5rem; }
.property-form button {
  padding: 0.5rem 1rem;
  background: #0d6efd;
  border: none;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}
.property-list { list-style: none; padding: 0; }
.property-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem;
  border-bottom: 1px solid #ddd;
}
.property-item button {
  background: #dc3545;
  color: white;
  border: none;
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
}
.error { color: red; margin-top: 1rem; }
</style>
