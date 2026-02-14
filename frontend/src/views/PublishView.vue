<template>
  <div class="page-header">
    <h1 class="page-title"> Pubblica un nuovo annuncio</h1>

    <!-- 🔹 Indicatore progresso -->
    <div class="steps-indicator">
      <span
        v-for="n in totalSteps"
        :key="n"
        :class="{ active: step === n }"
        class="step-dot"
      >
        {{ n }}
      </span>
    </div>


    <!-- Step 1 -->
    <div v-if="step === 1" class="step">
      <h2 class="page-subtitle">Informazioni di base</h2>
      <input v-model="form.title" placeholder="Titolo annuncio" required />
      <textarea
        v-model="form.description"
        placeholder="Descrizione immobile"
        rows="4"
        class="description-input"
        required
      ></textarea>
      <input v-model="form.city" placeholder="Città (Es. Napoli)" required />

      <div class="nav-buttons">
        <button
          class="btn-primary"
          :disabled="!form.title || !form.description || !form.city"
          @click="nextStep"
        >
          Avanti ➡️
        </button>
      </div>
    </div>

    <!-- Step 2: Indirizzo -->
    <div v-if="step === 2" class="step">
      <h2 class="page-subtitle">Indirizzo</h2>
      <input v-model="addressInput" placeholder="Via Roma 10, Napoli" required />
      <button class="third-btn" @click="resolveAddress">Trova sulla mappa</button>

      <div v-if="coords" id="map" class="map-preview"></div>
      <p v-if="coords" class="success">
        Coordinate trovate: {{ coords.lat }}, {{ coords.lng }}
      </p>

      <div class="nav-buttons">
        <button class="btn-primary" @click="prevStep">⬅️ Indietro</button>
        <button class="btn-primary" :disabled="!coords" @click="nextStep">
          Avanti ➡️
        </button>
      </div>
    </div>

    <!-- Step 3: Dettagli tecnici -->
    <div v-if="step === 3" class="step">
      <h2 class="page-subtitle">Dettagli immobile</h2>
      <input
        v-model.number="form.area"
        type="number"
        placeholder="Superficie (mq)"
        min="1"
        required
      />
      <input
        v-model.number="form.price"
        type="number"
        placeholder="Prezzo (€)"
        min="1"
        required
      />

      <select v-model="form.listingType" required>
        <option disabled value="">-- Tipo annuncio --</option>
        <option value="vendita">Vendita</option>
        <option value="affitto">Affitto</option>
      </select>

      <input
        v-model.number="form.rooms"
        type="number"
        min="1"
        placeholder="Numero stanze"
        required
      />

      <select v-model="form.energyClass" required>
        <option disabled value="">-- Classe energetica --</option>
        <option>A</option><option>B</option><option>C</option>
        <option>D</option><option>E</option><option>F</option><option>G</option>
      </select>

      <div class="nav-buttons">
        <button class="btn-primary" @click="prevStep">Indietro</button>
        <button
          class="btn-primary"
          :disabled="!form.area || !form.price || !form.rooms || !form.energyClass"
          @click="nextStep"
        >
          Avanti
        </button>
      </div>
    </div>

    <!-- Step 4: Foto -->
    <div v-if="step === 4" class="step">
      <h2 class="page-subtitle">Foto immobile</h2>
      <input type="file" accept="image/jpeg" @change="handleFileSelect" />
      <p class="hint">Carica una foto in formato .jpg</p>
      
      <div v-if="previewUrl" class="image-preview">
        <img :src="previewUrl" alt="Anteprima" />
      </div>

       <div class="nav-buttons">
        <button class="btn-primary" @click="prevStep">Indietro</button>
        <button class="btn-primary" @click="nextStep">Avanti</button>
      </div>
    </div>

    <!-- Step 5: Riepilogo -->
    <div v-if="step === 5" class="step">
      <h2>Riepilogo finale</h2>

      <div class="container">
        <ul class="summary">
          <li><b>Titolo:</b> {{ form.title }}</li>
          <li><b>Descrizione:</b> {{ form.description }}</li>
          <li><b>Città:</b> {{ form.city }}</li>
          <li><b>Indirizzo:</b> {{ form.address }}</li>
          <li><b>Superficie:</b> {{ form.area }} m²</li>
          <li><b>Prezzo:</b> € {{ form.price?.toLocaleString() ?? "" }}</li>
          <li><b>Tipo:</b> {{ form.listingType }}</li>
          <li><b>Stanze:</b> {{ form.rooms }}</li>
          <li><b>Classe energetica:</b> {{ form.energyClass }}</li>
        </ul>

        <div v-if="coords" id="map-summary" class="map-summary"></div>
      </div>

      <div class="nav-buttons">
        <button class="btn-primary" @click="prevStep">Indietro</button>
        <button class="btn-publish" @click="publish">Pubblica</button>
      </div>
    </div>

    <p v-if="error" class="error-message">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, nextTick, watch } from "vue"
import { usePropertyStore } from "@/stores/properties"
import { useAuthStore } from "@/stores/authenticate" 
import { useToast } from "vue-toastification"
import L from "leaflet"
import "leaflet/dist/leaflet.css"

const store = usePropertyStore()
const authStore = useAuthStore() 
const toast = useToast()

const step = ref(1)
const totalSteps = 5
const error = ref("")

const selectedFile = ref<File | null>(null)
const previewUrl = ref<string | null>(null)

function handleFileSelect(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    const file = target.files[0]
    if (!file.name.toLowerCase().endsWith(".jpg")) {
      toast.error("Solo file .jpg sono ammessi")
      target.value = ""
      return
    }
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const addressInput = ref("")
const coords = ref<{ lat: number; lng: number } | null>(null)
let map: L.Map | null = null

const form = reactive({
  title: "",
  description: "",
  city: "",
  address: "",
  area: null as number | null,
  price: null as number | null,
  latitude: null as number | null,
  longitude: null as number | null,
  listingType: "vendita" as "vendita" | "affitto",
  rooms: null as number | null,
  energyClass: "A",
  idUser: 0,    //  ID utente
  views: 0      // : Visualizzazioni (sempre 0 alla creazione)
})

// --- Step control ---
function nextStep() {
  if (step.value < totalSteps) {
    step.value++
    toast.success(`Passato allo step ${step.value}! 🎉`, { timeout: 2000 })
  }
}
function prevStep() {
  if (step.value > 1) step.value--
}

async function resolveAddress() {
  try {
    const res = await fetch(
      `https://api.geoapify.com/v1/geocode/search?text=${encodeURIComponent(
        addressInput.value
      )}&apiKey=c4dc78950f8f486cbf36cb126f4efda1`
    )
    const data = await res.json()

    if (data.features.length === 0) throw new Error("Indirizzo non trovato")

    const [lng, lat] = data.features[0].geometry.coordinates
    coords.value = { lat, lng }
    form.latitude = lat
    form.longitude = lng
    form.address = addressInput.value

    // mostra mappa preview
    await nextTick()
    if (!map) {
      map = L.map("map").setView([lat, lng], 15)
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "© OpenStreetMap contributors"
      }).addTo(map)
    }
    L.marker([lat, lng]).addTo(map).bindPopup(addressInput.value).openPopup()
    map.setView([lat, lng], 15)

    toast.success("📍 Indirizzo trovato!")
  } catch (e: any) {
    error.value = e.message || "Errore nel geocoding"
    toast.error("❌ Indirizzo non valido")
  }
}

watch(step, async (newStep) => {
  if (newStep === 5 && coords.value) {
    await nextTick()
    const mapSummary = L.map("map-summary", {
      zoomControl: false,
      attributionControl: false
    }).setView([coords.value.lat, coords.value.lng], 14)

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(mapSummary)
    L.marker([coords.value.lat, coords.value.lng]).addTo(mapSummary).bindPopup(form.address)
  }
})


async function publish() {
  try {
    //  Usa authStore.user?.id 
    if (!authStore.user?.id) {
      toast.error("❌ Devi essere loggato per pubblicare un annuncio")
      return
    }

    form.idUser = authStore.user.id  // Prende l'ID dall'utente loggato
    form.views = 0   
    const created = await store.addProperty(form)
    
    if (selectedFile.value && created && created.id) {
        await store.uploadImage(created.id, selectedFile.value)
    }

    toast.success("Annuncio pubblicato con successo 🎉")

    // reset
    step.value = 1
    form.title = ""
    form.description = ""
    form.city = ""
    form.address = ""
    form.area = 0
    form.price = 0
    form.latitude = null
    form.longitude = null
    form.idUser = 0    // : Reset
    form.views = 0     //  Reset
    addressInput.value = ""
    coords.value = null
    selectedFile.value = null
    previewUrl.value = null
    if (map) {
      map.remove()
      map = null
    }
  } catch (e) {
    error.value = "Errore durante la pubblicazione"
    toast.error("❌ Errore durante la pubblicazione")
  }
}
</script>

<style scoped>


/* Step indicator */
.steps-indicator {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 2rem;
}
.step-dot {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}
.step-dot.active {
  background: #0c5db1;
  color: white;
}

.step {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.step input,
.step textarea,
.step select {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  width: 100%;
}
textarea {
  resize: vertical;
  min-height: 80px;
}

/* Map preview */
.map-preview,
.map-summary {
  height: 250px;
  border-radius: 12px;
  margin-top: 1rem;
}

/* Summary */

.summary {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
  line-height: 1.6;
}

.error {
  color: red;
  margin-top: 1rem;
  text-align: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
  margin-top: 1rem;
}
</style>
