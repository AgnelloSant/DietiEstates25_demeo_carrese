<template>
  <div class="home">
    <!-- Hero magico con particelle animate -->
    <header class="hero-magical">
      <div class="particles-bg">
        <div class="particle" v-for="n in 15" :key="n" :style="getParticleStyle(n)"></div>
      </div>
      
      <div class="hero-content">
        <div class="floating-elements">
          <div class="house-icon">🏠</div>
          <div class="location-pin">📍</div>
          <div class="key-icon">🔑</div>
        </div>
        
        <div class="hero-main">
         
          <h1 class="hero-title">
            Dove i <span class="gradient-text">sogni</span> 
            <br>diventano <span class="gradient-text">casa</span>
          </h1>
          
          <p class="subtitle">
            Scopri la più grande selezione di immobili curata personalmente per te. 
            Ogni casa racconta una storia, qual è la tua?
          </p>
          
          <div class="hero-stats-inline">
            <div class="stat-bubble" v-for="stat in stats" :key="stat.label">
              <span class="stat-number">{{ stat.number }}</span>
              <span class="stat-label">{{ stat.label }}</span>
            </div>
          </div>
          
          <div class="hero-cta">
            <button @click="scrollToAnnunci" class="cta-primary">
              <span>Inizia il viaggio</span>
              <div class="cta-arrow">↓</div>
            </button>
            <a href="/publish" class="cta-ghost">Pubblica annuncio</a>
          </div>
        </div>
      </div>
      
      <div class="hero-visual">
        <div class="floating-house">
          <div class="house-base"></div>
          <div class="house-roof"></div>
          <div class="house-window"></div>
          <div class="house-door"></div>
        </div>
        <div class="floating-clouds">
          <div class="cloud" v-for="n in 3" :key="n"></div>
        </div>
      </div>
    </header>

    <!-- Filtri -->
    <div class="page-container">
    <div class="filters-section">
        <div class="page-header">
          <h3 class="page-title">Trova la tua casa ideale</h3>
          <FiltersBar
            :city="city"
            :minArea="minArea"
            :maxPrice="maxPrice"
            :listingType="listingType"
            :rooms="rooms"
            :energyClass="energyClass"
            @search="search"
          />
        </div>
      
    </div>

    <!-- Preferiti -->
    <section class="section" v-if="isLoggedIn">
      <div class="wide-content-container">
        <div class="section-header">
          <h2>I tuoi preferiti
            <small v-if="!store.favLoading && !store.favError">({{ store.favList.length }})</small>
          </h2>
          <button class="special-btn" @click="refreshFavs">
            <span class="refresh-icon">↻</span>
            Aggiorna
          </button>
        </div>

        <div v-if="store.favLoading" class="fav-skeleton">
          <div v-for="n in 3" :key="n" class="skeleton-card"></div>
        </div>

        <div v-else-if="store.favError" class="error-state">
          <p>{{ store.favError }}</p>
        </div>

        <div v-else-if="store.favList.length" class="fav-carousel">
          <PropertyCard
            v-for="p in store.favList"
            :key="`fav-${p.id}`"
            :property="p"
            :compact="true"
            class="fav-item"
          />
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">💙</div>
          <p>Nessun preferito salvato</p>
          <small>Clicca sul cuore degli annunci per salvarli qui</small>
        </div>
      </div>
    </section>

    <!-- Annunci in evidenza -->
    <section class="section" ref="annunciSection">
      <div class="wide-content-container">
        <div class="section-header">
          <div>
            <h2>Annunci in evidenza</h2>
            <p class="section-subtitle">Selezionati personalmente dal nostro team</p>
          </div>
          <div class="pagination-controls" v-if="!store.loading && totalPages > 1">
            <span class="text-info">Pagina {{ currentPage }} di {{ totalPages }}</span>
            <div class="page-buttons">
              <button class="page-btn" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">
                ←
              </button>
              <button class="page-btn" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">
                →
              </button>
            </div>
          </div>
        </div>

        <!-- Loading skeleton -->
        <div v-if="store.loading" class="properties-grid">
          <div v-for="n in pageSize" :key="n" class="skeleton-property"></div>
        </div>

        <!-- Error state -->
        <div v-else-if="store.error" class="error-state">
          <h3>Oops! Qualcosa è andato storto</h3>
          <p>{{ store.error }}</p>
          <button @click="store.fetchList()" class="retry-btn">Riprova</button>
        </div>

        <!-- Properties grid -->
        <div v-else-if="pagedList.length" class="properties-grid">
          <PropertyCard
            v-for="p in pagedList"
            :key="p.id"
            :property="p"
            @add-fav="store.addToFavourites"
            class="property-item"
          />
        </div>

        <!-- Empty state -->
        <div v-else class="empty-state">
          <div class="empty-icon">🏡</div>
          <h3>Nessun immobile trovato</h3>
          <p>Prova a modificare i filtri di ricerca</p>
          <button @click="resetFilters" class="reset-btn">Reset filtri</button>
        </div>

    <!-- Pagination full -->
<nav v-if="!store.loading && totalPages > 1" class="pagination-full">
  <button class="page-btn" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">
    Precedente
  </button>

  <div class="page-numbers">
    <button
      v-for="page in visiblePages"
      :key="page"
      class="page-number"
      :class="{ active: page === currentPage }"
      @click="goToPage(page)"
    >
      {{ page }}
    </button>
  </div>

  <button class="page-btn" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">
    Successivo
  </button>
</nav>

      </div>
    </section>
  </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue"
import { usePropertyStore } from "@/stores/properties"
import { useAuthStore } from "@/stores/authenticate"
import FiltersBar from "@/components/FiltersBar.vue"
import PropertyCard from "@/components/PropertyCard.vue"

const auth = useAuthStore()
const isLoggedIn = auth.isLoggedIn
const store = usePropertyStore()
const annunciSection = ref<HTMLElement>()

// Filtri
const city = ref("")
const minArea = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const listingType = ref<string>("")
const rooms = ref<number | null>(null)
const energyClass = ref<string>("")

// Paginazione
const currentPage = ref(1)
const pageSize = ref(10)

// Hero stats
const stats = ref([
  { number: '2K+', label: 'Proprietà' },
  { number: '500+', label: 'Clienti felici' },
  { number: '15', label: 'Anni esperienza' }
])

// Computed
const totalPages = computed(() => {
  return Math.max(1, Math.ceil(store.list.length / pageSize.value))
})

const pagedList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return store.list.slice(start, start + pageSize.value)
})

const visiblePages = computed(() => {
  const pages: number[] = []
  const total = totalPages.value
  const curr = currentPage.value
  const range = 2
  
  for (let p = Math.max(1, curr - range); p <= Math.min(total, curr + range); p++) {
    pages.push(p)
  }
  
  if (!pages.includes(1)) pages.unshift(1)
  if (!pages.includes(total) && total > 1) pages.push(total)
  
  return [...new Set(pages)].sort((a, b) => a - b)
})

// Methods
const getParticleStyle = (n: number) => {
  return {
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${3 + Math.random() * 4}s`
  }
}

const scrollToAnnunci = () => {
  annunciSection.value?.scrollIntoView({ 
    behavior: 'smooth', 
    block: 'start' 
  })
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    // Scroll dolce senza essere troppo invadente
    window.scrollTo({ 
      top: annunciSection.value?.offsetTop! - 100, 
      behavior: 'smooth' 
    })
  }
}

const search = (payload: any) => {
  currentPage.value = 1
  store.fetchList(payload)
}

const refreshFavs = () => {
  store.fetchFavList()
}

const resetFilters = () => {
  city.value = ""
  minArea.value = null
  maxPrice.value = null
  listingType.value = ""
  rooms.value = null
  energyClass.value = ""
  search({})
}

// Watch for list changes
watch(() => store.list.length, () => {
  currentPage.value = 1
})

// Mount
onMounted(() => {
  store.fetchList()
  if (isLoggedIn) store.fetchFavList()
})
</script>

<style scoped>
/* 
  Welcome page, utilizzato solamente qui inutile dunque migrarlo in css
 */

.particles-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: float linear infinite;
}

@keyframes float {
  0% { transform: translateY(100vh) translateX(0); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(-10vh) translateX(100px); opacity: 0; }
}

.hero-content {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 3rem;
  align-items: center;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.floating-elements {
  position: absolute;
  top: -50px;
  right: 10%;
  z-index: 1;
}

.floating-elements > div {
  position: absolute;
  font-size: 2rem;
  animation: bounce 3s infinite;
}

.house-icon { 
  top: 0; 
  left: 0; 
  animation-delay: 0s; 
}

.location-pin { 
  top: -20px; 
  left: 60px; 
  animation-delay: 1s; 
}

.key-icon { 
  top: 40px; 
  left: 30px; 
  animation-delay: 2s; 
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-20px); }
  60% { transform: translateY(-10px); }
}

@keyframes pulse {
  0% { transform: scale(0.8); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.5; }
  100% { transform: scale(0.8); opacity: 1; }
}

.hero-title {
  font-size: clamp(2.5rem, 5vw, 4rem);
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 1.5rem;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.gradient-text {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-stats-inline {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 3rem;
}


.hero-cta {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.cta-primary {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #1a202c;
  border: none;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 8px 30px rgba(255, 215, 0, 0.3);
}

.cta-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(255, 215, 0, 0.4);
}

.cta-arrow {
  animation: bounce-arrow 2s infinite;
}

@keyframes bounce-arrow {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(3px); }
  60% { transform: translateY(1px); }
}

.cta-ghost {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-weight: 600;
  padding: 1rem 2rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50px;
  transition: all 0.3s ease;
}

.cta-ghost:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
}

/* Hero visual */
.hero-visual {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.floating-house {
  position: relative;
  animation: float-house 6s ease-in-out infinite;
}

@keyframes float-house {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(2deg); }
}

.house-base {
  width: 120px;
  height: 80px;
  background: linear-gradient(145deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 8px;
  position: relative;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.house-roof {
  width: 0;
  height: 0;
  border-left: 70px solid transparent;
  border-right: 70px solid transparent;
  border-bottom: 50px solid #ff6b6b;
  position: absolute;
  top: -45px;
  left: -10px;
}

.house-window, .house-door {
  position: absolute;
  background: #ffd700;
  border-radius: 4px;
}

.house-window {
  width: 20px;
  height: 20px;
  top: 15px;
  right: 20px;
}

.house-door {
  width: 25px;
  height: 40px;
  bottom: 0;
  left: 20px;
  border-radius: 4px 4px 0 0;
}

.floating-clouds {
  position: absolute;
  width: 100%;
  height: 100%;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50px;
  opacity: 0.3;
}

.cloud:nth-child(1) {
  width: 60px;
  height: 20px;
  top: 20%;
  right: 10%;
  animation: float-cloud 8s linear infinite;
}

.cloud:nth-child(2) {
  width: 40px;
  height: 15px;
  top: 40%;
  right: 60%;
  animation: float-cloud 10s linear infinite reverse;
}

.cloud:nth-child(3) {
  width: 80px;
  height: 25px;
  top: 60%;
  right: 20%;
  animation: float-cloud 12s linear infinite;
}

@keyframes float-cloud {
  0% { transform: translateX(0); }
  100% { transform: translateX(50px); }
}

/* Sections */
.filters-section {
  padding: 3rem 0;
  margin-top: -1px;
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: -6%;
}

.refresh-icon {
  transition: transform 0.3s ease;
}

.fav-skeleton, .fav-carousel {
  display: flex;
  gap: 1.5rem;
  max-width: 100%;
  overflow-x: auto;
  box-sizing: border-box;
  scroll-snap-type: x mandatory;
  padding-bottom: 1rem;
}

.retry-btn, .reset-btn {
  background: #0c5db1;
  color: white;
  border: none;
  padding: 0.8rem 2rem;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.3s ease;
}

.retry-btn:hover, .reset-btn:hover {
  background: #094a88;
}

/* Pagination */

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.page-buttons {
  display: flex;
  gap: 0.5rem;
}

.pagination-full {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 3rem;
  flex-wrap: wrap;
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
}

.page-btn, .page-number {
  background: white;
  border: 1px solid #d0d5dd;
  color: #344054;
  padding: 0.5rem 0.8rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  min-width: 40px;
  text-align: center;
}

.page-btn:hover:not(:disabled), .page-number:hover {
  border-color: #0c5db1;
  background: #f0f6ff;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: #f9fafb;
}

.page-number.active {
  background: #0c5db1;
  border-color: #0c5db1;
  color: white;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-content {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 2rem;
    padding: 2rem 1rem;
  }
  
  .hero-visual {
    order: -1;
  }
  
  .hero-stats-inline {
    justify-content: center;
  }
  
  .hero-cta {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .properties-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .pagination-full {
    flex-direction: column;
    gap: 1rem;
  }
 
}
</style>