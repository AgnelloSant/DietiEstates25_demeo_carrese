<template>
  <div class="publish-wrapper">
    <div class="publish-form-card">
      <div class="page-header">
        <h1 class="page-title">Pubblica un nuovo annuncio</h1>

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
      </div>

    <!-- Step 1 -->
<div v-if="step === 1" class="step">
  <h2 class="page-subtitle">Informazioni di base</h2>

  <div class="form-grid single">
    <div class="form-group">
      <label>Titolo annuncio</label>
      <input v-model="form.title" placeholder="Es. Trilocale luminoso con terrazzo" required />
    </div>

    <div class="form-group">
      <label>Città</label>
      <input v-model="form.city" placeholder="Es. Napoli" required />
    </div>

    <div class="form-group full">
      <label>Descrizione</label>
      <textarea
        v-model="form.description"
        placeholder="Descrivi le caratteristiche principali dell'immobile"
        rows="5"
        class="description-input"
        required
      ></textarea>
    </div>
  </div>

  <div class="nav-buttons">
    <button
      class="btn-primary"
      :disabled="!form.title || !form.description || !form.city"
      @click="nextStep"
    >
      Avanti
    </button>
  </div>
</div>

    <!-- Step 2: Indirizzo -->
<div v-if="step === 2" class="step">
  <h2 class="page-subtitle">Indirizzo</h2>

  <div class="form-grid single">
    <div class="form-group full">
      <label>Indirizzo completo</label>
      <input v-model="addressInput" placeholder="Es. Via Roma 10, Napoli" required />
    </div>
  </div>

  <div class="inline-actions">
    <button class="third-btn" @click="resolveAddress">Trova sulla mappa</button>
  </div>

  <div v-if="coords" id="map" class="map-preview"></div>

  <p v-if="coords" class="success">
    Coordinate trovate: {{ coords.lat }}, {{ coords.lng }}
  </p>

  <div class="nav-buttons">
    <button class="btn-secondary" @click="prevStep">Indietro</button>
    <button class="btn-primary" :disabled="!coords" @click="nextStep">
      Avanti
    </button>
  </div>
</div>

    <!-- Step 3: Dettagli tecnici -->
<div v-if="step === 3" class="step">
  <h2 class="page-subtitle">Dettagli immobile</h2>

  <div class="form-grid">
    <div class="form-group">
      <label>Superficie (mq)</label>
      <input
        v-model.number="form.area"
        type="number"
        placeholder="Es. 120"
        min="1"
        required
      />
    </div>

    <div class="form-group">
      <label>Prezzo (€)</label>
      <input
        v-model.number="form.price"
        type="number"
        placeholder="Es. 250000"
        min="1"
        required
      />
    </div>

    <div class="form-group">
      <label>Tipo annuncio</label>
      <select v-model="form.listingType" required>
        <option disabled value="">-- Tipo annuncio --</option>
        <option value="vendita">Vendita</option>
        <option value="affitto">Affitto</option>
      </select>
    </div>

    <div class="form-group">
      <label>Numero stanze</label>
      <input
        v-model.number="form.rooms"
        type="number"
        min="1"
        placeholder="Es. 4"
        required
      />
    </div>

    <div class="form-group full">
      <label>Classe energetica</label>
      <select v-model="form.energyClass" required>
        <option disabled value="">-- Classe energetica --</option>
        <option>A</option><option>B</option><option>C</option>
        <option>D</option><option>E</option><option>F</option><option>G</option>
      </select>
    </div>
  </div>

  <div class="nav-buttons">
    <button class="btn-secondary" @click="prevStep">Indietro</button>
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
<!-- Step 4: Foto -->
<div v-if="step === 4" class="step">
  <h2 class="page-subtitle">Foto immobile</h2>

  <div class="form-group full">
    <label>Carica immagini</label>
    <input
      ref="fileInput"
      type="file"
      accept=".jpg,.jpeg,image/jpeg"
      multiple
      @change="handleFileSelect"
    />
    <p class="hint">Carica una o più foto in formato .jpg o .jpeg</p>
  </div>

  <div v-if="previewUrls.length" class="image-preview-grid">
    <div
      v-for="(url, index) in previewUrls"
      :key="index"
      class="preview-item"
    >
      <img
        :src="url"
        alt="Anteprima"
        class="preview-thumb"
      />
      <button
        type="button"
        class="remove-preview-btn"
        @click="removeSelectedImage(index)"
      >
        ✕
      </button>
    </div>
  </div>

  <p v-if="selectedFiles.length" class="hint">
    Hai selezionato {{ selectedFiles.length }} foto
  </p>

  <div class="nav-buttons">
    <button class="btn-secondary" @click="prevStep">Indietro</button>
    <button class="btn-primary" @click="nextStep">Avanti</button>
  </div>
</div>


    <!-- Step 5: Riepilogo -->
  <div v-if="step === 5" class="step">
  <h2 class="page-subtitle">Riepilogo finale</h2>

  <div class="summary-layout">
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
      <li><b>Foto selezionate:</b> {{ selectedFiles.length }}</li>
    </ul>

    <div v-if="coords" id="map-summary" class="map-summary"></div>
  </div>

  <div class="nav-buttons">
    <button class="btn-secondary" @click="prevStep">Indietro</button>
    <button class="btn-publish" @click="publish" :disabled="isPublishing">
      {{ isPublishing ? "Pubblicazione..." : "Pubblica" }}
    </button>
  </div>
</div>

      <p v-if="error" class="error-message">{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, nextTick, watch, onBeforeUnmount } from "vue"
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
const isPublishing = ref(false)

const selectedFiles = ref<File[]>([])
const previewUrls = ref<string[]>([])
const fileInput = ref<HTMLInputElement | null>(null)

const addressInput = ref("")
const coords = ref<{ lat: number; lng: number } | null>(null)

let map: L.Map | null = null
let mapSummary: L.Map | null = null
let mapMarker: L.Marker | null = null
let summaryMarker: L.Marker | null = null

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
  idUser: 0,
  views: 0
})

function clearPreviewUrls() {
  previewUrls.value.forEach((url) => URL.revokeObjectURL(url))
  previewUrls.value = []
}

function handleFileSelect(event: Event) {
  const target = event.target as HTMLInputElement
  const files = target.files ? Array.from(target.files) : []

  if (!files.length) return

  const invalidFile = files.find((file) => {
    const name = file.name.toLowerCase()
    return !name.endsWith(".jpg") && !name.endsWith(".jpeg")
  })

  if (invalidFile) {
    toast.error("Solo file .jpg o .jpeg sono ammessi")
    target.value = ""
    return
  }

  // Evita duplicati banali per nome+size
  const newFiles = files.filter((newFile) => {
    return !selectedFiles.value.some(
      (existingFile) =>
        existingFile.name === newFile.name &&
        existingFile.size === newFile.size
    )
  })

  if (!newFiles.length) {
    target.value = ""
    return
  }

  selectedFiles.value = [...selectedFiles.value, ...newFiles]
  previewUrls.value = [
    ...previewUrls.value,
    ...newFiles.map((file) => URL.createObjectURL(file))
  ]

  // reset del campo input per permettere di riselezionare anche lo stesso file dopo
  target.value = ""
}

function nextStep() {
  if (step.value < totalSteps) {
    step.value++
  }
}

function prevStep() {
  if (step.value > 1) {
    step.value--
  }
}

async function resolveAddress() {
  try {
    error.value = ""

    const res = await fetch(
      `https://api.geoapify.com/v1/geocode/search?text=${encodeURIComponent(
        addressInput.value
      )}&apiKey=c4dc78950f8f486cbf36cb126f4efda1`
    )

    const data = await res.json()

    if (!data.features || data.features.length === 0) {
      throw new Error("Indirizzo non trovato")
    }

    const [lng, lat] = data.features[0].geometry.coordinates

    coords.value = { lat, lng }
    form.latitude = lat
    form.longitude = lng
    form.address = addressInput.value

    await nextTick()

    if (!map) {
      map = L.map("map").setView([lat, lng], 15)
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "© OpenStreetMap contributors"
      }).addTo(map)
    }

    if (mapMarker) {
      map.removeLayer(mapMarker)
    }

    mapMarker = L.marker([lat, lng]).addTo(map).bindPopup(addressInput.value)
    mapMarker.openPopup()
    map.setView([lat, lng], 15)

    toast.success("Indirizzo trovato")
  } catch (e: any) {
    error.value = e?.message || "Errore nel geocoding"
    toast.error(error.value)
  }
}

watch(step, async (newStep) => {
  if (newStep === 5 && coords.value) {
    await nextTick()

    if (!mapSummary) {
      mapSummary = L.map("map-summary", {
        zoomControl: false,
        attributionControl: false
      }).setView([coords.value.lat, coords.value.lng], 14)

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(mapSummary)
    }

    if (summaryMarker) {
      mapSummary.removeLayer(summaryMarker)
    }

    summaryMarker = L.marker([coords.value.lat, coords.value.lng])
      .addTo(mapSummary)
      .bindPopup(form.address)

    mapSummary.setView([coords.value.lat, coords.value.lng], 14)
  }
})

function resetForm() {
  step.value = 1
  error.value = ""

  form.title = ""
  form.description = ""
  form.city = ""
  form.address = ""
  form.area = null
  form.price = null
  form.latitude = null
  form.longitude = null
  form.listingType = "vendita"
  form.rooms = null
  form.energyClass = "A"
  form.idUser = 0
  form.views = 0

  addressInput.value = ""
  coords.value = null
  selectedFiles.value = []

  clearPreviewUrls()

  if (fileInput.value) {
    fileInput.value.value = ""
  }

  if (map) {
    map.remove()
    map = null
    mapMarker = null
  }

  if (mapSummary) {
    mapSummary.remove()
    mapSummary = null
    summaryMarker = null
  }
}

async function publish() {
  if (isPublishing.value) return

  try {
    error.value = ""

    if (!authStore.user?.id) {
      toast.error("Devi essere loggato per pubblicare un annuncio")
      return
    }

    isPublishing.value = true

    form.idUser = authStore.user.id
    form.views = 0

    const created = await store.addProperty(form)

    if (selectedFiles.value.length > 0 && created?.id != null) {
      const propertyId = created.id

      for (const file of selectedFiles.value) {
        await store.uploadImage(propertyId, file)
      }
    }

    toast.success("Annuncio pubblicato con successo")
    resetForm()
  } catch (e: any) {
    error.value = e?.message || "Errore durante la pubblicazione"
    toast.error(error.value)
  } finally {
    isPublishing.value = false
  }
}

onBeforeUnmount(() => {
  clearPreviewUrls()

  if (map) {
    map.remove()
    map = null
  }

  if (mapSummary) {
    mapSummary.remove()
    mapSummary = null
  }
})


function removeSelectedImage(index: number) {
  URL.revokeObjectURL(previewUrls.value[index])
  previewUrls.value.splice(index, 1)
  selectedFiles.value.splice(index, 1)
}

</script>


<style scoped>

/* HEADER */
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* BOTTONE PREFERITI */
.favorite-btn {
  background: #0c5db1;
  border: none;
  cursor: pointer;
  font-size: 1.5rem;
  color: #ccc;
  border-radius: 10px;
  padding: 6px 10px;
}

.favorite-btn:hover {
  color: #e63946;
}

/* === IMMAGINE PRINCIPALE === */
.detail-image {
  margin-bottom: 1.5rem;
}

.main-image-wrapper {
  position: relative;
}

.main-image {
  width: 100%;
  height: 420px;
  object-fit: cover;
  border-radius: 12px;
  display: block;
}

/* === FRECCE === */
.nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  
  width: 46px;
  height: 46px;
  border-radius: 50%;

  background: rgba(12, 93, 177, 0.9);
  color: white;
  font-size: 1.6rem;

  border: none;
  cursor: pointer;
  z-index: 2;

  display: flex;
  align-items: center;
  justify-content: center;

  /* FIX ALLINEAMENTO */
  line-height: 1;
  padding: 0;
}

.nav-left {
  left: 12px;
}

.nav-right {
  right: 12px;
}

.nav:hover {
  background: #094a88;
}

/* === INFO === */
.detail-city {
  font-size: 1.1rem;
  color: #555;
}

/* === GALLERY === */
.gallery {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.gallery-thumb {
  width: 90px;
  height: 65px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid transparent;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.gallery-thumb:hover {
  border-color: #0c5db1;
  transform: scale(1.05);
}

.gallery-thumb.active {
  border-color: #0c5db1;
}

/* === VANTAGGI === */
.advantages {
  margin-top: 20px;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background: #f9f9f9;
}

.advantages h3 {
  margin-bottom: 10px;
  color: #0c5db1;
}

/* === LOADING === */
.loading {
  text-align: center;
  margin-top: 3rem;
  font-size: 1.2rem;
}

/* === INPUT MODALI === */
.input-row {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 15px;
}

.input-row input {
  flex: 1;
  min-width: 0;
}

/* === HINT === */
.hint {
  color: #6b7280;
  opacity: 0.7;
  font-style: italic;
}

/* === MAPPA === */
.detail-map {
  height: 400px;
  border-radius: 12px;
  margin-top: 1.5rem;
}

/* === RESPONSIVE === */
@media (max-width: 768px) {
  .main-image {
    height: 260px;
  }

  .nav {
    width: 38px;
    height: 38px;
    font-size: 1.2rem;
  }

  .gallery-thumb {
    width: 70px;
    height: 55px;
  }
}

</style>