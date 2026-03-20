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
      <input
        v-model="form.title"
        placeholder="Es. Trilocale luminoso con terrazzo"
        required
      />
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
      :disabled="!form.title || !form.description"
      @click="nextStep"
    >
      Avanti
    </button>
  </div>
</div>

      <!-- Step 2 -->
   <div v-if="step === 2" class="step">
  <h2 class="page-subtitle">Indirizzo</h2>

  <div class="form-grid">
    <div class="form-group">
      <label>Città</label>
      <input
        v-model="form.city"
        placeholder="Es. Napoli"
        required
      />
    </div>

    <div class="form-group">
      <label>Via / indirizzo</label>
      <input
        v-model="addressInput"
        placeholder="Es. Via Marina 12"
        required
      />
    </div>
    <p class="hint">Puoi inserire anche solo la via, ma con il civico la ricerca è più precisa</p>
  </div>

  <div class="inline-actions">
    <button
      class="third-btn"
      :disabled="!form.city || !addressInput"
      @click="resolveAddress"
    >
      Trova sulla mappa
    </button>
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

      <!-- Step 3 -->
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
              <option>A</option>
              <option>B</option>
              <option>C</option>
              <option>D</option>
              <option>E</option>
              <option>F</option>
              <option>G</option>
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

      <!-- Step 4 -->
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

      <!-- Step 5 -->
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

  target.value = ""
}

function removeSelectedImage(index: number) {
  URL.revokeObjectURL(previewUrls.value[index])
  previewUrls.value.splice(index, 1)
  selectedFiles.value.splice(index, 1)
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

//per la query di ricerca allo step 2 
async function resolveAddress() {
  try {
    error.value = ""

    const city = form.city.trim()
    const street = addressInput.value.trim()

    if (!city || !street) {
      throw new Error("Inserisci città e indirizzo")
    }

    const attempts = [
      `${street}, ${city}`,
      `${street} ${city}`,
      street
    ]

    let data: any = null

    for (const query of attempts) {
      const res = await fetch(
        `https://api.geoapify.com/v1/geocode/search?text=${encodeURIComponent(
          query
        )}&apiKey=c4dc78950f8f486cbf36cb126f4efda1`
      )

      const currentData = await res.json()

      if (currentData?.features && currentData.features.length > 0) {
        data = currentData
        break
      }
    }

    if (!data || !data.features || data.features.length === 0) {
      throw new Error("Indirizzo non trovato")
    }

    const [lng, lat] = data.features[0].geometry.coordinates

    coords.value = { lat, lng }
    form.latitude = lat
    form.longitude = lng
    form.address = street

    const popupAddress = `${street}, ${city}`

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

    mapMarker = L.marker([lat, lng]).addTo(map).bindPopup(popupAddress)
    mapMarker.openPopup()
    map.setView([lat, lng], 15)

    toast.success("Indirizzo trovato")
  } catch (e: any) {
    error.value = e?.message || "Errore nel geocoding"
    toast.error(error.value)
  }
}

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
</script>

<style scoped>
.publish-wrapper {
  display: flex;
  justify-content: center;
  padding: 2rem 1rem 3rem;
}

.publish-form-card {
  width: 100%;
  max-width: 900px;
  background: #fff;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.steps-indicator {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin: 1.5rem 0 2rem;
}

.step-dot {
  width: 34px;
  height: 34px;
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
  gap: 1.25rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem 1.25rem;
}

.form-grid.single {
  grid-template-columns: 1fr;
}

.form-group.full {
  grid-column: 1 / -1;
}

.description-input {
  min-height: 120px;
  resize: vertical;
}

.inline-actions {
  display: flex;
  justify-content: flex-start;
}

.map-preview,
.map-summary {
  height: 260px;
  border-radius: 12px;
  overflow: hidden;
}

.summary-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.25rem;
  align-items: start;
}

.summary {
  list-style: none;
  padding: 1rem;
  margin: 0;
  background: #f8fafc;
  border-radius: 12px;
  line-height: 1.8;
  text-align: left;
}

.hint {
  color: #64748b;
  font-size: 0.9rem;
  margin-top: 0.25rem;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  gap: 12px;
  margin-top: 0.5rem;
  align-items: start;
}

.preview-item {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  overflow: hidden;
  border-radius: 10px;
  border: 1px solid #ddd;
  background: #f8fafc;
}

.preview-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.remove-preview-btn {
  position: absolute;
  top: 6px;
  right: 6px;
  border: none;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 0.9rem;
  line-height: 1;
  padding: 0;
}

.btn-publish {
  background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
  color: white;
}

.btn-publish:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .publish-form-card {
    padding: 1.25rem;
  }

  .form-grid,
  .summary-layout {
    grid-template-columns: 1fr;
  }
}
</style>