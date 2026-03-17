<template>
  <div class="home">
    <!-- Hero professionale centrato -->
    <header class="hero-professional">
      <!-- Sfondo animato con gradiente fluido -->
      <div class="hero-background">
        <div class="gradient-orb orb-1"></div>
        <div class="gradient-orb orb-2"></div>
        <div class="gradient-orb orb-3"></div>
      </div>
      
      <div class="hero-content-center">
        <div class="hero-text-center">
          <h1 class="hero-title-main">
            Trova la casa dei tuoi <span class="accent-gradient">sogni</span>
          </h1>
          
          <p class="hero-description">
            Piattaforma completa per la ricerca e pubblicazione di immobili. 
            Strumenti avanzati, processo semplice, risultati concreti.
          </p>
          
          <!-- Features/Benefits invece di stats falsi -->
          <div class="hero-features">
            <div class="feature-item">
              <div class="feature-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="8"></circle>
                  <path d="m21 21-4.35-4.35"></path>
                </svg>
              </div>
              <div class="feature-text">
                <div class="feature-title">Ricerca Avanzata</div>
                <div class="feature-desc">Filtri intelligenti</div>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                </svg>
              </div>
              <div class="feature-text">
                <div class="feature-title">Sicuro e Affidabile</div>
                <div class="feature-desc">Dati protetti</div>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                  <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                </svg>
              </div>
            </div>
          </div>
          
          <!-- CTA -->
          <div class="hero-cta-center">
            <button @click="scrollToAnnunci" class="btn-hero-primary">
              Esplora immobili
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </button>
            <a href="/publish" class="btn-hero-secondary">
              Pubblica annuncio
            </a>
          </div>
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
      <section class="section-h" v-if="isLoggedIn">
        <div class="wide-horizontal-container">
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
    :is-favourite="true" 
    @toggle-fav="store.toggleFavourite"
    class="fav-item"
  />
</div>

          <div v-else class="empty-state">
            <div class="empty-icon"></div>
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
    :is-favourite="store.favList.some(fav => fav.id === p.id)"
    @toggle-fav="store.toggleFavourite"
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
const scrollToAnnunci = () => {
  annunciSection.value?.scrollIntoView({ 
    behavior: 'smooth', 
    block: 'start' 
  })
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
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
.hero-professional {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  color: white;
  padding: 2rem;
}

/* Sfondo con sfere gradient animate */
.hero-background {
  position: absolute;
  inset: 0;
  overflow: hidden;
  z-index: 1;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.25;
  animation: float-orb 20s ease-in-out infinite;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  top: -15%;
  left: -10%;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  top: 30%;
  right: -10%;
  animation-delay: 7s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  bottom: -15%;
  left: 25%;
  animation-delay: 14s;
}

@keyframes float-orb {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -40px) scale(1.15); }
  66% { transform: translate(-30px, 30px) scale(0.85); }
}

/* Content centrato */
.hero-content-center {
  position: relative;
  z-index: 2;
  max-width: 900px;
  margin: 0 auto;
  text-align: center;
}

.hero-text-center {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Title principale */
.hero-title-main {
  font-size: clamp(2.5rem, 7vw, 5.5rem);
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 1.5rem;
  letter-spacing: -0.03em;
}

.accent-gradient {
  background: linear-gradient(135deg, #667eea, #f093fb, #ffd89b);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient-shift 3s ease infinite;
  background-size: 200% 200%;
}

@keyframes gradient-shift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

/* Description */
.hero-description {
  font-size: 1.25rem;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 4rem;
  max-width: 650px;
  margin-left: auto;
  margin-right: auto;
}

/* Features (sostituzione delle stats false) */
.hero-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
  padding: 3rem 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-5px);
  border-color: rgba(255, 255, 255, 0.2);
}

.feature-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

.feature-icon svg {
  color: white;
}

.feature-text {
  text-align: left;
  flex: 1;
}

.feature-title {
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
  color: white;
}

.feature-desc {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.6);
}

/* CTA Actions */
.hero-cta-center {
  display: flex;
  gap: 1rem;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-hero-primary {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1.2rem 2.8rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 14px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
}

.btn-hero-primary::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #764ba2, #667eea);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.btn-hero-primary:hover::before {
  opacity: 1;
}

.btn-hero-primary:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 18px 45px rgba(102, 126, 234, 0.5);
}

.btn-hero-primary svg {
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.btn-hero-primary:hover svg {
  transform: translateX(6px);
}

.btn-hero-secondary {
  padding: 1.2rem 2.5rem;
  color: white;
  text-decoration: none;
  font-weight: 600;
  font-size: 1.1rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 14px;
  transition: all 0.3s ease;
  display: inline-block;
}

.btn-hero-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-3px);
}

/* ============================================
   📋 SEZIONI RESTO PAGINA
   ============================================ */

.filters-section {
  padding: 4rem 0 2rem;
  margin-top: -1px;
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: -6%;
}

.fav-carousel {
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

/* ============================================
   📱 RESPONSIVE
   ============================================ */

@media (max-width: 768px) {
  .hero-professional {
    padding: 2rem 1rem;
  }
  
  .hero-title-main {
    font-size: 2.5rem;
  }
  
  .hero-description {
    font-size: 1.05rem;
    margin-bottom: 2.5rem;
  }
  
  .hero-features {
    grid-template-columns: 1fr;
    gap: 1rem;
    padding: 2rem 0;
  }
  
  .hero-cta-center {
    flex-direction: column;
    width: 100%;
  }
  
  .btn-hero-primary,
  .btn-hero-secondary {
    width: 100%;
    justify-content: center;
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
