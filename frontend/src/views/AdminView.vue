<template>
  <div class="admin-container">
    <!-- ✨ Header con titolo e stats -->
    <div class="admin-header">
      <h1 class="admin-title">Dashboard Admin - Dieti Estates</h1>
      <div class="stats-bar">
        <div class="stat-card">
          <span class="stat-number">{{ list.length }}</span>
          <span class="stat-label">Proprietà Totali</span>
        </div>
        <div class="stat-card">
          <span class="stat-number">{{ venditeCount }}</span>
          <span class="stat-label">In Vendita</span>
        </div>
        <div class="stat-card">
          <span class="stat-number">{{ affittiCount }}</span>
          <span class="stat-label">In Affitto</span>
        </div>
        <div class="stat-card">
          <span class="stat-number">€{{ averagePrice.toLocaleString('it-IT') }}</span>
          <span class="stat-label">Prezzo Medio</span>
        </div>
      </div>
    </div>

    <!-- 🔍 Barra controlli -->
    <div class="controls-section">
      <div class="filter-row">
        <div class="search-container">
          <input 
            v-model="searchQuery" 
            placeholder="Cerca per titolo, città o indirizzo..." 
            class="search-input"
          >
        </div>
        
        <select v-model="filterType" class="filter-select">
          <option value="">Tutti i tipi</option>
          <option value="vendita">Vendita</option>
          <option value="affitto">Affitto</option>
        </select>

        <select v-model="sortBy" class="sort-select">
          <option value="price">Prezzo</option>
          <option value="area">Superficie</option>
          <option value="title">Nome A-Z</option>
        </select>

        <button @click="toggleSortOrder" class="sort-toggle-btn" :title="sortOrder === 'desc' ? 'Crescente' : 'Decrescente'">
          {{ sortOrder === 'desc' ? '↓' : '↑' }}
        </button>

        <button 
          @click="toggleSelectionMode" 
          class="selection-toggle-btn"
          :class="{ active: selectionMode }"
        >
          {{ selectionMode ? 'Annulla Selezione' : 'Seleziona Multipla' }}
        </button>
      </div>

      <!-- Azioni bulk -->
      <div class="bulk-actions" v-if="selectedProperties.length > 0">
        <div class="selected-info">
          <span>{{ selectedProperties.length }} proprietà selezionate</span>
          <button @click="clearSelection" class="btn-clear">Deseleziona tutto</button>
        </div>
        <button @click="bulkDelete" class="btn-bulk-delete">
          Elimina selezionate ({{ selectedProperties.length }})
        </button>
      </div>
    </div>

    <!-- Grid delle proprietà -->
    <div v-if="sortedAndFilteredProperties.length > 0" class="properties-grid">
      <div 
        v-for="property in sortedAndFilteredProperties" 
        :key="property.id"
        class="property-card"
        :class="{ 
          'selected': selectedProperties.includes(property.id),
          'selection-mode': selectionMode 
        }"
        @click="handleCardClick(property)"
      >
        <!-- Checkbox che appare solo in modalità selezione -->
        <div v-if="selectionMode" class="selection-checkbox" @click.stop>
          <input 
            type="checkbox" 
            :value="property.id"
            v-model="selectedProperties"
            class="property-checkbox"
          >
        </div>

        <!-- Badge tipo e stato -->
        <div class="badges-container">
          <div class="property-badge" :class="property.listingType">
            {{ property.listingType === 'vendita' ? 'Vendita' : 'Affitto' }}
          </div>
          
          <div class="status-badges">
            <span class="badge popular" v-if="isPopular(property)">Popolare</span>
            <span class="badge premium" v-if="isPremium(property)">Premium</span>
          </div>
        </div>

        <!-- Contenuto principale -->
        <div class="property-content">
          <h3 class="property-title">{{ property.title }}</h3>
          <p class="property-location">{{ property.city }}</p>
          <p class="property-address">{{ property.address || 'Indirizzo non disponibile' }}</p>
          
          <div class="property-details">
            <div class="detail-item">
              <span class="detail-label">Superficie:</span>
              <span>{{ property.area }} m²</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Stanze:</span>
              <span>{{ property.rooms }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Classe energetica:</span>
              <span>{{ property.energyClass }}</span>
            </div>
          </div>

          <div class="property-price">
            €{{ property.price.toLocaleString('it-IT') }}
            <span class="price-per-sqm">
              (€{{ Math.round(property.price / property.area) }}/m²)
            </span>
          </div>

          <div class="property-services" v-if="hasNearbyServices(property)">
            <div class="service-badges">
              <span v-if="property.nearSchool" class="service-badge">Scuola</span>
              <span v-if="property.nearPark" class="service-badge">Parco</span>
              <span v-if="property.nearTransport" class="service-badge">Trasporti</span>
            </div>
          </div>
        </div>

        <!-- Azioni rapide -->
        <div class="property-actions" @click.stop v-if="!selectionMode">
          <button 
            class="action-btn view-btn" 
            @click="goToDetail(property.id)"
            title="Visualizza dettagli"
          >
            Visualizza
          </button>
          <button 
            class="action-btn edit-btn" 
            @click="openEditModal(property)"
            title="Modifica proprietà"
          >
            Modifica
          </button>
          <button 
            class="action-btn delete-btn" 
            @click="confirmDelete(property)"
            title="Elimina proprietà"
          >
            Elimina
          </button>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div v-else class="empty-state">
      <div class="empty-icon">🏠</div>
      <h3>Nessuna proprietà trovata</h3>
      <p>{{ searchQuery ? 'Prova a cambiare i filtri di ricerca' : 'Non ci sono proprietà da gestire al momento' }}</p>
      <button @click="clearAllFilters" class="btn-clear-filters" v-if="hasActiveFilters">
        Rimuovi tutti i filtri
      </button>
    </div>

    <!-- 🔧 MODALE DI MODIFICA -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
      <div class="edit-modal">
        <div class="modal-header">
          <h2>Modifica Proprietà</h2>
          <button @click="closeEditModal" class="close-btn">✖</button>
        </div>
        
        <form @submit.prevent="saveEdit" class="edit-form">
          <div class="form-row">
            <div class="form-group">
              <label>Titolo *</label>
              <input v-model="editForm.title" required />
            </div>
            <div class="form-group">
              <label>Città *</label>
              <input v-model="editForm.city" required />
            </div>
          </div>
          
          <div class="form-group">
            <label>Indirizzo *</label>
            <input v-model="editForm.address" required />
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Superficie (m²) *</label>
              <input v-model.number="editForm.area" type="number" min="1" required />
            </div>
            <div class="form-group">
              <label>Prezzo (€) *</label>
              <input v-model.number="editForm.price" type="number" min="1" required />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Tipo annuncio *</label>
              <select v-model="editForm.listingType" required>
                <option value="vendita">Vendita</option>
                <option value="affitto">Affitto</option>
              </select>
            </div>
            <div class="form-group">
              <label>Numero stanze *</label>
              <input v-model.number="editForm.rooms" type="number" min="1" required />
            </div>
          </div>
          
          <div class="form-group">
            <label>Classe energetica *</label>
            <select v-model="editForm.energyClass" required>
              <option>A</option><option>B</option><option>C</option>
              <option>D</option><option>E</option><option>F</option><option>G</option>
            </select>
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeEditModal" class="btn-cancel">Annulla</button>
            <button type="submit" class="btn-save" :disabled="editLoading">
              {{ editLoading ? 'Salvando...' : 'Salva Modifiche' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Popup conferma eliminazione -->
    <ConfirmDialog
      :visible="showDeleteDialog"
      :title="`Elimina ${propertyToDelete?.title || 'proprietà'}`"
      message="Sei sicuro di voler eliminare questa proprietà? L'azione non può essere annullata."
      @confirm="handleDeleteConfirm"
      @cancel="showDeleteDialog = false"
    />

    <ConfirmDialog
      :visible="showBulkDeleteDialog"
      :title="`Elimina ${selectedProperties.length} proprietà`"
      message="Sei sicuro di voler eliminare tutte le proprietà selezionate? L'azione non può essere annullata."
      @confirm="handleBulkDeleteConfirm"
      @cancel="showBulkDeleteDialog = false"
    />

    <!-- Messaggi di stato -->
    <div v-if="error" class="error-message">{{ error }}</div>
    <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from "vue"
import { useRouter } from "vue-router"
import { storeToRefs } from "pinia"
import { usePropertyStore } from "@/stores/properties"
import ConfirmDialog from "@/components/ConfirmDialog.vue"
import type { PropertySearchDTO, PropertyUpdateDTO } from "@/types/Properties"

const router = useRouter()
const store = usePropertyStore()
const { list } = storeToRefs(store)

// Stati locali esistenti
const error = ref("")
const successMessage = ref("")
const searchQuery = ref("")
const filterType = ref("")
const sortBy = ref("price")
const sortOrder = ref("desc")
const selectedProperties = ref<number[]>([])
const showDeleteDialog = ref(false)
const showBulkDeleteDialog = ref(false)
const propertyToDelete = ref<PropertySearchDTO | null>(null)
const selectionMode = ref(false)

// ✅ Stati per la modifica (con tutti i campi richiesti)
const showEditModal = ref(false)
const editLoading = ref(false)
const editingPropertyId = ref<number | null>(null) // ✅ Teniamo traccia dell'ID
const editForm = ref<PropertyUpdateDTO>({
  title: "",
  city: "", 
  address: "",
  area: 0,
  price: 0,
  latitude: null,
  longitude: null,
  nearSchool: false,
  nearPark: false,
  nearTransport: false,
  listingType: "vendita" as "vendita" | "affitto", // ✅ Tipizzazione corretta
  rooms: 1,
  energyClass: "A",
  idUser: 1, // ✅ Campo obbligatorio aggiunto
  views: 0   // ✅ Campo opzionale aggiunto
})

// Computed (stesso codice)
const venditeCount = computed(() => 
  list.value.filter(p => p.listingType === 'vendita').length
)
const affittiCount = computed(() => 
  list.value.filter(p => p.listingType === 'affitto').length
)
const averagePrice = computed(() => {
  if (list.value.length === 0) return 0
  const total = list.value.reduce((sum, p) => sum + p.price, 0)
  return Math.round(total / list.value.length)
})

const filteredProperties = computed(() => {
  let filtered = list.value

  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.title.toLowerCase().includes(query) ||
      p.city.toLowerCase().includes(query) ||
      (p.address && p.address.toLowerCase().includes(query))
    )
  }

  if (filterType.value) {
    filtered = filtered.filter(p => p.listingType === filterType.value)
  }

  return filtered
})

const sortedAndFilteredProperties = computed(() => {
  const sorted = [...filteredProperties.value].sort((a, b) => {
    let aValue, bValue

    switch (sortBy.value) {
      case 'price':
        aValue = a.price
        bValue = b.price
        break
      case 'area':
        aValue = a.area
        bValue = b.area
        break
      case 'title':
      default:
        aValue = a.title.toLowerCase()
        bValue = b.title.toLowerCase()
        break
    }

    if (sortOrder.value === 'asc') {
      return aValue > bValue ? 1 : -1
    } else {
      return aValue < bValue ? 1 : -1
    }
  })

  return sorted
})

const hasActiveFilters = computed(() => 
  searchQuery.value.trim() !== '' || filterType.value !== ''
)

// Helper functions (stesso codice)
const isPopular = (property: PropertySearchDTO) => {
  return property.price > averagePrice.value * 1.5 && property.area > 100
}

const isPremium = (property: PropertySearchDTO) => {
  return property.energyClass === 'A' && property.price > averagePrice.value * 1.8
}

const hasNearbyServices = (property: PropertySearchDTO) => {
  return property.nearSchool || property.nearPark || property.nearTransport
}

// Funzioni esistenti
const toggleSelectionMode = () => {
  selectionMode.value = !selectionMode.value
  if (!selectionMode.value) {
    selectedProperties.value = []
  }
}

const handleCardClick = (property: PropertySearchDTO) => {
  if (selectionMode.value) {
    const index = selectedProperties.value.indexOf(property.id)
    if (index > -1) {
      selectedProperties.value.splice(index, 1)
    } else {
      selectedProperties.value.push(property.id)
    }
  } else {
    goToDetail(property.id)
  }
}

const goToDetail = (id: number) => {
  router.push({ name: 'property-detail', params: { id: String(id) } })
}

// ✅ CORRETTE - Funzioni per la modifica
const openEditModal = (property: PropertySearchDTO) => {
  editingPropertyId.value = property.id // ✅ Salva l'ID
  editForm.value = {
    title: property.title,
    city: property.city,
    address: property.address || "",
    area: property.area,
    price: property.price,
    latitude: property.latitude || null,
    longitude: property.longitude || null,
    nearSchool: property.nearSchool || false,
    nearPark: property.nearPark || false,
    nearTransport: property.nearTransport || false,
    listingType: (property.listingType || "vendita") as "vendita" | "affitto", // ✅ Cast corretto
    rooms: property.rooms || 1,
    energyClass: property.energyClass || "A",
    idUser: 1, // ✅ Per ora hardcoded, poi puoi usare l'ID dell'admin loggato
    views: 0   // ✅ Mantenere views correnti o 0
  }
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
  editingPropertyId.value = null
  // ✅ Reset corretto con tutti i campi
  editForm.value = {
    title: "",
    city: "",
    address: "",
    area: 0,
    price: 0,
    latitude: null,
    longitude: null,
    nearSchool: false,
    nearPark: false,
    nearTransport: false,
    listingType: "vendita",
    rooms: 1,
    energyClass: "A",
    idUser: 1,
    views: 0
  }
}

const saveEdit = async () => {
  if (!editForm.value.title || !editForm.value.city || !editingPropertyId.value) return
  
  editLoading.value = true
  try {
    // ✅ Usa l'ID salvato invece di cercare la proprietà
    await store.editProperty(editingPropertyId.value, editForm.value)
    showEditModal.value = false
    editingPropertyId.value = null
    successMessage.value = "Proprietà modificata con successo!"
    setTimeout(() => successMessage.value = "", 3000)
  } catch (err) {
    error.value = "Errore durante la modifica della proprietà"
    console.error(err)
  } finally {
    editLoading.value = false
  }
}

// Resto delle funzioni (stesso codice)
const confirmDelete = (property: PropertySearchDTO) => {
  propertyToDelete.value = property
  showDeleteDialog.value = true
}

const handleDeleteConfirm = async () => {
  if (!propertyToDelete.value) return

  try {
    await store.removeProperty(propertyToDelete.value.id)
    showDeleteDialog.value = false
    propertyToDelete.value = null
    error.value = ""
    successMessage.value = "Proprietà eliminata con successo"
    setTimeout(() => successMessage.value = "", 3000)
  } catch (err) {
    error.value = "Errore durante l'eliminazione della proprietà"
    console.error(err)
  }
}

const bulkDelete = () => {
  if (selectedProperties.value.length === 0) return
  showBulkDeleteDialog.value = true
}

const handleBulkDeleteConfirm = async () => {
  try {
    for (const id of selectedProperties.value) {
      await store.removeProperty(id)
    }
    const deletedCount = selectedProperties.value.length
    selectedProperties.value = []
    showBulkDeleteDialog.value = false
    selectionMode.value = false
    error.value = ""
    successMessage.value = `${deletedCount} proprietà eliminate con successo`
    setTimeout(() => successMessage.value = "", 3000)
  } catch (err) {
    error.value = "Errore durante l'eliminazione delle proprietà"
    console.error(err)
  }
}

const clearSelection = () => {
  selectedProperties.value = []
}

const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
}

const clearAllFilters = () => {
  searchQuery.value = ""
  filterType.value = ""
  sortBy.value = "price"
  sortOrder.value = "desc"
}

watch(error, (newError) => {
  if (newError) {
    setTimeout(() => error.value = "", 5000)
  }
})

onMounted(() => {
  store.fetchList()
})
</script>

<style scoped>
/* Tutti gli stili esistenti rimangono identici, aggiungo solo quelli per la modale */

.admin-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.admin-header {
  text-align: center;
  margin-bottom: 3rem;
}

.admin-title {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 1.5rem;
}

.stats-bar {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.stat-card {
  background: white;
  padding: 1.2rem 1.5rem;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease;
  min-width: 140px;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-number {
  display: block;
  font-size: 1.8rem;
  font-weight: 800;
  color: #667eea;
}

.stat-label {
  display: block;
  font-size: 0.85rem;
  color: #64748b;
  margin-top: 0.5rem;
}

/* ✅ STILI MODALE DI MODIFICA */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  backdrop-filter: blur(4px);
}

.edit-modal {
  background: white;
  border-radius: 16px;
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem 2rem 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  margin: 0;
  color: #1f2937;
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #374151;
}

.edit-form {
  padding: 2rem;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.form-group label {
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #374151;
}

.form-group input,
.form-group select {
  padding: 0.75rem;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-save {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.btn-save:hover:not(:disabled) {
  background: #5a67d8;
}

.btn-save:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

/* Resto degli stili esistenti... */
.controls-section {
  margin-bottom: 2rem;
}

.filter-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  align-items: center;
}

.search-container {
  flex: 1;
  min-width: 300px;
}

.search-input, .filter-select, .sort-select {
  padding: 0.8rem 1.2rem;
  border: none;
  border-radius: 12px;
  background: white;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  font-size: 0.95rem;
  transition: box-shadow 0.3s ease;
}

.search-input {
  width: 100%;
}

.search-input:focus, .filter-select:focus, .sort-select:focus {
  outline: none;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.sort-toggle-btn {
  background: white;
  border: none;
  border-radius: 12px;
  width: 48px;
  height: 48px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  font-size: 1.2rem;
  font-weight: bold;
  color: #667eea;
  transition: all 0.3s ease;
}

.sort-toggle-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.selection-toggle-btn {
  background: white;
  border: 2px solid #667eea;
  color: #667eea;
  padding: 0.8rem 1.5rem;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.selection-toggle-btn:hover {
  background: #667eea;
  color: white;
}

.selection-toggle-btn.active {
  background: #667eea;
  color: white;
}

.bulk-actions {
  background: rgba(239, 68, 68, 0.1);
  border: 2px solid rgba(239, 68, 68, 0.2);
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.selected-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #dc2626;
  font-weight: 600;
}

.btn-clear {
  background: transparent;
  border: 1px solid #dc2626;
  color: #dc2626;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.8rem;
}

.btn-bulk-delete {
  background: #dc2626;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.3s ease;
}

.btn-bulk-delete:hover {
  background: #b91c1c;
}

.properties-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.property-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  cursor: pointer;
  position: relative;
  border: 2px solid transparent;
}

.property-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.25);
}

.property-card.selected {
  border-color: #667eea;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.property-card.selection-mode {
  cursor: pointer;
}

.selection-checkbox {
  position: absolute;
  top: 1rem;
  left: 1rem;
  z-index: 3;
  background: white;
  border-radius: 8px;
  padding: 0.5rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.property-checkbox {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.badges-container {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  z-index: 2;
}

.property-badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.property-badge.vendita {
  background: rgba(34, 197, 94, 0.9);
  color: white;
}

.property-badge.affitto {
  background: rgba(59, 130, 246, 0.9);
  color: white;
}

.status-badges {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.badge {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.7rem;
  font-weight: 600;
  text-align: center;
}

.badge.popular {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
}

.badge.premium {
  background: linear-gradient(135deg, #ffd43b, #fab005);
  color: #333;
}

.property-content {
  padding: 2rem;
}

.property-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: #1e293b;
}

.property-location {
  color: #667eea;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.property-address {
  color: #64748b;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
}

.property-details {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #64748b;
}

.detail-label {
  font-weight: 600;
  color: #374151;
}

.property-price {
  font-size: 1.8rem;
  font-weight: 800;
  color: #667eea;
  margin-bottom: 1rem;
}

.price-per-sqm {
  font-size: 0.9rem;
  color: #94a3b8;
  font-weight: 400;
}

.property-services {
  margin-top: 1rem;
}

.service-badges {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.service-badge {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.property-actions {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  display: flex;
  gap: 0.5rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.property-card:hover .property-actions {
  opacity: 1;
}

.action-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: 600;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.view-btn {
  background: rgba(59, 130, 246, 0.9);
  color: white;
}

.edit-btn {
  background: rgba(251, 191, 36, 0.9);
  color: white;
}

.delete-btn {
  background: rgba(239, 68, 68, 0.9);
  color: white;
}

.action-btn:hover {
  transform: translateY(-2px);
  opacity: 1;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: #64748b;
  margin-bottom: 1rem;
}

.empty-state p {
  color: #94a3b8;
  margin-bottom: 1.5rem;
}

.btn-clear-filters {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}

.error-message, .success-message {
  padding: 1rem;
  border-radius: 12px;
  margin-top: 2rem;
  text-align: center;
  font-weight: 600;
}

.error-message {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.success-message {
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
  border: 1px solid rgba(34, 197, 94, 0.2);
}

@media (max-width: 768px) {
  .admin-container {
    padding: 1rem;
  }
  
  .properties-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .stats-bar {
    gap: 0.5rem;
  }
  
  .stat-card {
    min-width: 120px;
    padding: 1rem;
  }
  
  .filter-row {
    flex-direction: column;
  }
  
  .search-container {
    min-width: unset;
  }
  
  .admin-title {
    font-size: 2rem;
  }
  
  .bulk-actions {
    flex-direction: column;
    text-align: center;
  }
  
  .edit-modal {
    width: 95%;
    margin: 1rem;
  }
  
  .form-row {
    flex-direction: column;
  }
}
</style>
