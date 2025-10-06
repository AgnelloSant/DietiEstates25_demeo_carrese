<template>
  <div v-if="property" class="detail-container">
    <!-- 🏠 Immagine -->
    <div class="detail-image">
      <img :src="property.imageUrl || '/placeholder-house.jpg'" alt="Foto immobile" />
    </div>

    <!-- 📋 Info principali -->
    <div class="detail-info">
      <div class="detail-header">
        <h1 class="detail-title">{{ property.title }}</h1>
        <button class="favorite-btn" @click="toggleFavorite(property.id)">
          <i class="fa-solid fa-heart"></i>
        </button>
      </div>
      <p class="detail-city"><i class="fa-solid fa-location-dot"></i> {{ property.city }}</p>
      <p class="detail-area"><strong>{{ property.area }} m²</strong></p>
      <p class="detail-price">€ {{ property.price.toLocaleString() }}</p>
      <p class="detail-extra"><b>Tipo:</b> {{ property.listingType }}</p>
      <p class="detail-extra"><b>Stanze:</b> {{ property.rooms }}</p>
      <p class="detail-extra"><b>Classe energetica:</b> {{ property.energyClass }}</p>
      <p class="detail-extra"><b>Indirizzo:</b> {{ property.address }}</p>
      <p v-if="property.description" class="detail-description">{{ property.description }}</p>
      <p v-if="property.publishedAt" class="detail-date">
        Pubblicato il {{ new Date(property.publishedAt).toLocaleDateString() }}
      </p>
    </div>

    <!-- 🔘 Bottoni azione -->
    <div class="button-row">
      <button @click="showReservationForm = true" class="btn">Effettua una prenotazione</button>
      <button @click="vendorProfile" class="btn">Dati venditore</button>
      <button @click="showBidForm = true" class="btn">Piazza un'offerta</button>
    </div>


    <!-- POPUP PRENOTAZIONE -->
    <div v-if="showReservationForm" class="modal-overlay">
      <div class="modal-content">
        <h3 class="font-bold mb-4">Scegli giorno e ora</h3>
        <div  class="input-row">
          <input type="date" v-model="selectedDate" />
          <input type="time" v-model="selectedTime" />
        </div>
        <div class="button-row">
          <button @click="showReservationForm = false" class="btn">
            Annulla
          </button>
          <button @click="confirmReservation" class="btn">
            Conferma
          </button>
        </div>
      </div>
    </div>

    <!--POPUP OFFERTA-->
    <div v-if="showBidForm" class="modal-overlay">
      <div class="modal-content" style="max-width: 400px;">
        <h3 class="font-bold mb-4">Quanto vuoi offrire?</h3>
        <div class="input-row">
          <input  type="number" v-model.number="offerAmount" min="1"></input>
          <span class="hint">Consigliato: {{ ((property.price*95)/100).toLocaleString() }}</span>
        </div>
        <div class="button-row">
          <button @click="showBidForm = false" class="btn">Annulla</button>
          <button class="btn" @click="confirmBid">Invia</button>
        </div>
      </div>
    </div>

    <!-- ✅ Vantaggi zona -->
    <div v-if="property.nearSchool || property.nearPark || property.nearTransport" class="advantages">
      <h3>Vantaggi della zona</h3>
      <ul>
        <li v-if="property.nearSchool">🏫 Vicino a scuole</li>
        <li v-if="property.nearPark">🌳 Vicino a parchi</li>
        <li v-if="property.nearTransport">🚌 Vicino a trasporti pubblici</li>
      </ul>
    </div>

    <!-- 🌍 Mappa -->
    <div
      v-if="property.latitude && property.longitude"
      id="map"
      style="height: 400px; border-radius: 12px; margin-top: 1.5rem"
    ></div>
    <p v-else class="detail-coords">📍 Coordinate non disponibili</p>
  </div>

  <!-- stato caricamento -->
  <div v-else class="loading">Caricamento...</div>
</template>

<script setup lang="ts">
import { onMounted, ref, nextTick, watch} from "vue"
import { httpProperty } from "@/api/http"
import type { PropertyDetailDTO } from "@/types/Properties"
import { usePropertyStore } from "@/stores/properties"
import { useToast } from "vue-toastification"



// 🗺️ Leaflet
import L from "leaflet"
import "leaflet/dist/leaflet.css"
import { text } from "stream/consumers"

delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png",
  iconUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png",
  shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png"
})

// Props route
const props = defineProps<{ id: string }>()
const property = ref<PropertyDetailDTO | null>(null)

// State
const showReservationForm = ref(false)
const showBidForm = ref(false)
const selectedDate = ref("")
const selectedTime = ref("")
const offerAmount = ref<number | null>(null)

const propertyStore = usePropertyStore()
const toast = useToast()

// Caricamento property
onMounted(async () => {
  const { data } = await httpProperty.get<PropertyDetailDTO>(`/properties/${props.id}`)
  property.value = data

  if (property.value?.latitude && property.value?.longitude) {
    await nextTick()
    const map = L.map("map").setView([property.value.latitude, property.value.longitude], 14)
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(map)
    L.marker([property.value.latitude, property.value.longitude]).addTo(map)
  }
})

watch(showReservationForm, (isOpen) => {
  if (isOpen) {
    document.body.style.overflow = "hidden"
  } else {
    document.body.style.overflow = ""
  }
})

watch(showBidForm, (isOpen) => { 
  if(isOpen) { 
    document.body.style.overflow = "hidden"
  }else{ 
    document.body.style.overflow = ""
  }
})

// Conferma prenotazione
async function confirmReservation() {
  if (!selectedDate.value || !selectedTime.value) {
    toast.warning("⚠️ Seleziona data e ora")
    return
  }
  try {
    await propertyStore.createAReservation({
      id_prop: Number(props.id),
      date: selectedDate.value,
      time: selectedTime.value
    })
    showReservationForm.value = false
    toast.success("✅ Prenotazione registrata con successo!")
  } catch (err) {
    console.error("Errore prenotazione:", err)
    toast.error("❌ Errore durante la prenotazione")
  }
}

async function confirmBid(){ 
  if(!offerAmount.value){ 
    toast.warning("Inserire un importo prima di procedere!")
    return
  }
  try{
    await propertyStore.createABid({
      id_prop: Number(props.id),
      amount: offerAmount.value
    })
    showBidForm.value = false
    toast.success("Offerta registrata con successo!")
  }catch(err){ 
    toast.error("Errore nell'inserimento dell'offerta")
  }
}

function vendorProfile() {
  // TODO: Apri profilo venditore
}

function toggleFavorite(idprop: number) {
  propertyStore.addToFavourites(idprop)
}
</script>

<style scoped>
.detail-container {
  max-width: 1000px;
  margin: 2rem auto;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background: #fff;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

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

.button-row {
  display: flex;
  justify-content: space-around;
  width: 100%;
}

.detail-image img {
  width: 100%;
  height: 420px;
  object-fit: cover;
  border-radius: 12px;
}

.detail-info {
  padding: 1rem 0;
  border-bottom: 1px solid #eee;
}

.detail-title {
  font-size: 2rem;
  font-weight: 700;
  color: #0c5db1;
}

.detail-city {
  font-size: 1.1rem;
  color: #555;
}

.detail-area,
.detail-price {
  font-size: 1.2rem;
}

.detail-price {
  font-weight: bold;
  color: #28a745;
}

.detail-description {
  font-size: 1rem;
  line-height: 1.6;
  color: #333;
}

.detail-date {
  font-size: 0.9rem;
  color: #777;
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

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-content {
  background: white;
  padding: 1.5rem;
  border-radius: 15px;
  width: 90%;
  max-width: 600px;
  max-height: 300px;
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

.hint{ 
  color: #6b7280;
  opacity: 0.7;
  font-style: italic;

}

</style>
