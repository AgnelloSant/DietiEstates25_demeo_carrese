<template>
  <div v-if="property" class="def-container">
    <!-- Immagine principale -->
    <div class="detail-image">
      <div class="main-image-wrapper">
        <button
          v-if="images.length > 1"
          class="nav nav-left"
          @click="prevImage"
        >
          ‹
        </button>

        <img
          :src="mainImageSrc"
          alt="Foto immobile"
          class="main-image"
        />

        <button
          v-if="images.length > 1"
          class="nav nav-right"
          @click="nextImage"
        >
          ›
        </button>
      </div>

      <!-- Thumbnails -->
      <div v-if="images.length > 1" class="gallery">
        <img
          v-for="(img, index) in images"
          :key="img"
          :src="getContentUrl(img)"
          class="gallery-thumb"
          :class="{ active: index === activeIndex }"
          @click="selectImage(index)"
        />
      </div>
    </div>

    <!-- Info principali -->
    <div class="info-container">
      <div class="detail-header">
        <h1 class="title">{{ property.title }}</h1>
        <button class="favorite-btn" @click="toggleFavorite(property.id)">
          <i class="fa-solid fa-heart"></i>
        </button>
      </div>

      <p class="detail-city">
        <i class="fa-solid fa-location-dot"></i> {{ property.city }}
      </p>

      <p class="bold-text"><strong>{{ property.area }} m²</strong></p>

      <p class="green-text">
        € {{ property.price != null ? Number(property.price).toLocaleString("it-IT") : "N/D" }}
      </p>

      <p class="detail-extra"><b>Tipo:</b> {{ property.listingType }}</p>
      <p class="detail-extra"><b>Stanze:</b> {{ property.rooms }}</p>
      <p class="detail-extra"><b>Classe energetica:</b> {{ property.energyClass }}</p>
      <p class="detail-extra"><b>Indirizzo:</b> {{ property.address }}</p>

      <p v-if="property.description" class="italic-text">
        {{ property.description }}
      </p>

      <p v-if="property.publishedAt" class="text-info">
        Pubblicato il {{ new Date(property.publishedAt).toLocaleDateString() }}
      </p>
    </div>

    <!-- Bottoni azione -->
    <div class="row-container">
      <button @click="showReservationForm = true" class="btn">
        Effettua una prenotazione
      </button>
      <button @click="showBidForm = true" class="btn">
        Piazza un'offerta
      </button>
    </div>

    <!-- Popup prenotazione -->
    <div v-if="showReservationForm" class="modal-overlay">
      <div class="modal modal--medium">
        <h2 class="title">Scegli giorno e ora</h2>
        <div class="input-row">
          <input type="date" v-model="selectedDate" />
          <input type="time" v-model="selectedTime" />
        </div>
        <div class="row-container">
          <button @click="showReservationForm = false" class="btn">
            Annulla
          </button>
          <button @click="confirmReservation" class="btn">
            Conferma
          </button>
        </div>
      </div>
    </div>

    <!-- Popup offerta -->
    <div v-if="showBidForm" class="modal-overlay">
      <div class="modal modal--small" style="max-width: 400px;">
        <h2 class="title">Quanto vuoi offrire?</h2>
        <div class="input-row">
          <input type="number" v-model.number="offerAmount" min="1" />
          <span class="hint">
            Consigliato: {{ ((property.price * 95) / 100).toLocaleString() }}
          </span>
        </div>
        <div class="row-container">
          <button @click="showBidForm = false" class="btn">Annulla</button>
          <button class="btn" @click="confirmBid">Invia</button>
        </div>
      </div>
    </div>

    <!-- Vantaggi zona -->
    <div
      v-if="property.nearSchool || property.nearPark || property.nearTransport"
      class="advantages"
    >
      <h3>Vantaggi della zona</h3>
      <ul>
        <li v-if="property.nearSchool">🏫 Vicino a scuole</li>
        <li v-if="property.nearPark">🌳 Vicino a parchi</li>
        <li v-if="property.nearTransport">🚌 Vicino a trasporti pubblici</li>
      </ul>
    </div>

    <!-- Mappa -->
    <div
      v-if="property.latitude && property.longitude"
      id="map"
      class="detail-map"
    ></div>
    <p v-else class="detail-coords">📍 Coordinate non disponibili</p>
  </div>

  <div v-else class="loading">Caricamento...</div>
</template>



<script setup lang="ts">
import { onMounted, ref, nextTick, watch, computed } from "vue"
import type { PropertyDetailDTO } from "@/types/Properties"
import { usePropertyStore } from "@/stores/properties"
import { useToast } from "vue-toastification"
import L from "leaflet"
import "leaflet/dist/leaflet.css"

delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png",
  iconUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png",
  shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png"
})

const props = defineProps<{ id: string }>()
const property = ref<PropertyDetailDTO | null>(null)

const showReservationForm = ref(false)
const showBidForm = ref(false)
const selectedDate = ref("")
const selectedTime = ref("")
const offerAmount = ref<number | null>(null)

const propertyStore = usePropertyStore()
const toast = useToast()

const images = ref<string[]>([])
const activeIndex = ref(0)

const mainImageSrc = computed(() => {
  if (images.value.length > 0) {
    return getContentUrl(images.value[activeIndex.value])
  }

  if (property.value?.imageUrl) {
    return getContentUrl(property.value.imageUrl)
  }

  return "/placeholder-house.jpg"
})

onMounted(async () => {
  property.value = await propertyStore.fetchDetail(Number(props.id))

  images.value = await propertyStore.fetchImages(Number(props.id))
  activeIndex.value = 0

  if (property.value?.latitude && property.value?.longitude) {
    await nextTick()
    const map = L.map("map").setView(
      [property.value.latitude, property.value.longitude],
      14
    )

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map)
    L.marker([property.value.latitude, property.value.longitude]).addTo(map)
  }
})

function getContentUrl(path: string) {
  const baseUrl = import.meta.env.VITE_API_PROPERTY_URL || "http://localhost:8082"
  return `${baseUrl}/uploads/${path}`
}

watch(showReservationForm, (isOpen) => {
  document.body.style.overflow = isOpen ? "hidden" : ""
})

watch(showBidForm, (isOpen) => {
  document.body.style.overflow = isOpen ? "hidden" : ""
})

async function confirmReservation() {
  if (!selectedDate.value || !selectedTime.value) {
    toast.warning("⚠️ Seleziona data e ora")
    return
  }

  try {
    const res = await propertyStore.createAReservation({
      id_prop: Number(props.id),
      date: selectedDate.value,
      time: selectedTime.value
    })

    if (res) {
      showReservationForm.value = false
      toast.success("✅ Prenotazione registrata con successo!")
    } else {
      toast.error("❌ Errore durante la prenotazione")
    }
  } catch (err) {
    console.error("Errore prenotazione:", err)
    toast.error("❌ Errore durante la prenotazione")
  }
}

async function confirmBid() {
  if (!offerAmount.value) {
    toast.warning("Inserire un importo prima di procedere!")
    return
  }

  try {
    const res = await propertyStore.createABid({
      id_prop: Number(props.id),
      amount: offerAmount.value
    })

    if (res) {
      showBidForm.value = false
      toast.success("Offerta registrata con successo!")
    } else {
      toast.error("Errore nell'inserimento dell'offerta")
    }
  } catch (err) {
    toast.error("Errore nell'inserimento dell'offerta")
  }
}

function toggleFavorite(idprop: number) {
  propertyStore.addToFavourites(idprop)
}

function selectImage(index: number) {
  activeIndex.value = index
}

function nextImage() {
  if (!images.value.length) return
  activeIndex.value = (activeIndex.value + 1) % images.value.length
}

function prevImage() {
  if (!images.value.length) return
  activeIndex.value = (activeIndex.value - 1 + images.value.length) % images.value.length
}
</script>


<style scoped>
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.favorite-btn {
  background: #0c5db1;
  border: none;
  cursor: pointer;
  font-size: 1.5rem;
  color: #ccc;
}

.favorite-btn:hover {
  color: #e63946;
}

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

.nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: rgba(12, 93, 177, 0.9);
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  z-index: 2;
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

.detail-city {
  font-size: 1.1rem;
  color: #555;
}

.advantages {
  margin-top: 20px;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.advantages h3 {
  margin-bottom: 10px;
  color: #0c5db1;
}

.loading {
  text-align: center;
  margin-top: 3rem;
  font-size: 1.2rem;
}

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

.hint {
  color: #6b7280;
  opacity: 0.7;
  font-style: italic;
}

.gallery {
  display: flex;
  gap: 10px;
  margin-top: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.gallery-thumb {
  width: 88px;
  height: 64px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid transparent;
  flex-shrink: 0;
}

.gallery-thumb:hover,
.gallery-thumb.active {
  border-color: #0c5db1;
}

.detail-map {
  height: 400px;
  border-radius: 12px;
  margin-top: 1.5rem;
}

@media (max-width: 768px) {
  .main-image {
    height: 280px;
  }

  .nav {
    width: 36px;
    height: 36px;
    font-size: 1.2rem;
  }
}
</style>