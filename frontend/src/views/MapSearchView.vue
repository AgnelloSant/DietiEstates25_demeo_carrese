<template>
  <div class="map-search-container">
    <!-- 🗺️ Mappa -->
    <div id="map" class="map"></div>

    <!-- 📋 Lista proprietà -->
    <div class="property-list">
      <h2>🏡 Proprietà trovate ({{ store.list.length }})</h2>

      <!-- 🔄 Reset -->
      <button class="btn-reset" @click="resetSearch">❌ Reset ricerca</button>

      <!-- Stato -->
      <div v-if="store.loading">Caricamento proprietà…</div>
      <div v-else-if="store.error" class="error">{{ store.error }}</div>
      <div v-else-if="!store.list.length">
        Disegna un cerchio sulla mappa per cercare immobili.
      </div>

      <!-- Lista -->
      <div v-else>
        <div
          v-for="p in store.list"
          :key="p.id"
          class="property-item"
          @click="focusOnProperty(p.id)"
        >
          <h3>{{ p.title }}</h3>
          <p>{{ p.city }} – {{ p.area }} m²</p>
          <p class="address">📍 {{ p.address }}</p>
          <p class="price">€ {{ p.price.toLocaleString() }}</p>
          <RouterLink :to="`/properties/${p.id}`" class="btn-details">
            🔎 Vedi dettagli
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue"
import { useRouter } from "vue-router"
import { usePropertyStore } from "@/stores/properties"
import L, { Circle, Map, Marker } from "leaflet"
import "leaflet/dist/leaflet.css"
import "leaflet-draw/dist/leaflet.draw.css"
import "leaflet-draw"

const store = usePropertyStore()
const router = useRouter()

let map: Map | null = null
let activeCircle: Circle | null = null
let markers: Record<number, Marker> = {}
let drawnItems: L.FeatureGroup | null = null

const INITIAL_CENTER: [number, number] = [41.9, 12.5]
const INITIAL_ZOOM = 6

onMounted(() => {
  map = L.map("map").setView(INITIAL_CENTER, INITIAL_ZOOM)
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "© OpenStreetMap contributors"
  }).addTo(map)

  drawnItems = new L.FeatureGroup()
  map.addLayer(drawnItems)

  const drawControl = new (L as any).Control.Draw({
    draw: {
      polygon: false,
      rectangle: false,
      polyline: false,
      marker: false,
      circlemarker: false,
      circle: { shapeOptions: { color: "#0c5db1" } }
    },
    edit: { featureGroup: drawnItems }
  })
  map.addControl(drawControl)

  map.on((L as any).Draw.Event.CREATED, handleDrawCreated)
})

async function handleDrawCreated(e: any) {
  if (e.layerType !== "circle") return

  if (activeCircle) map?.removeLayer(activeCircle)
  activeCircle = e.layer as Circle
  if (drawnItems) drawnItems.addLayer(activeCircle)

  const center = activeCircle.getLatLng()
  const radiusKm = activeCircle.getRadius() / 1000
  console.log("Cerchio selezionato:", center, radiusKm)

  await store.fetchListByBounds(center.lat, center.lng, radiusKm)

  refreshMarkers()
}

function refreshMarkers() {
  Object.values(markers).forEach(m => map?.removeLayer(m))
  markers = {}

  store.list.forEach(createPropertyMarker)
}

function createPropertyMarker(p: any) {
  if (!p.latitude || !p.longitude) return

  const marker = L.marker([p.latitude, p.longitude])
  marker.bindPopup(`
    <b>${p.title}</b><br>
    ${p.city}<br>
    📍 ${p.address}<br>
    € ${p.price.toLocaleString()}<br>
    <button id="goto-${p.id}" class="popup-btn">Dettagli</button>
  `)
  marker.addTo(map!)
  markers[p.id] = marker

  marker.on("popupopen", () => setupPopupClick(p.id))
}

function setupPopupClick(propertyId: number) {
  const btn = document.getElementById(`goto-${propertyId}`)
  if (btn) {
    btn.addEventListener("click", () => router.push(`/properties/${propertyId}`))
  }
}

function resetSearch() {
  if (activeCircle && map) {
    map.removeLayer(activeCircle)
    activeCircle = null
  }
  Object.values(markers).forEach(m => map?.removeLayer(m))
  markers = {}
  store.list = []
  map?.setView(INITIAL_CENTER, INITIAL_ZOOM)
}

function focusOnProperty(id: number) {
  const marker = markers[id]
  if (map && marker) {
    map.setView(marker.getLatLng(), 15)
    marker.openPopup()
  }
}
</script>

<style scoped>
.map-search-container {
  display: flex;
  gap: 1rem;
}

.map {
  flex: 2;
  height: 80vh;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

/* Lista */
.property-list {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 1rem;
  height: 80vh;
  overflow-y: auto;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}
.property-item {
  border-bottom: 1px solid #eee;
  padding: 0.8rem 0;
  cursor: pointer;
}
.property-item:hover {
  background: #f9f9f9;
}
.property-item:last-child {
  border-bottom: none;
}
.price {
  font-weight: bold;
  color: #28a745;
}
.address {
  font-size: 0.85rem;
  color: #555;
}
.error {
  color: red;
  font-weight: 600;
}
.btn-reset {
  margin-bottom: 1rem;
  padding: 0.6rem 1.2rem;
  background: #d9534f;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  width: 100%;
}
.btn-reset:hover {
  background: #c9302c;
}
.btn-details {
  display: inline-block;
  margin-top: 0.4rem;
  padding: 0.4rem 0.8rem;
  background: #0c5db1;
  color: #fff;
  font-size: 0.85rem;
  border-radius: 6px;
  text-decoration: none;
}
.btn-details:hover {
  background: #094a88;
}
.popup-btn {
  margin-top: 6px;
  padding: 4px 8px;
  background: #0c5db1;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 0.8rem;
  cursor: pointer;
}
.popup-btn:hover {
  background: #094a88;
}
</style>
