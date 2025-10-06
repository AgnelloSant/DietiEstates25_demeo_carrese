// src/stores/properties.ts
import L, { Map as LeafletMap } from "leaflet" 
import { defineStore } from "pinia"
import { searchProperties, createProperty, updateProperty, deleteProperty, createReservation, 
      getReservationsByProperty, getReservationsByUser, createBid, getBidsByProperty,
      getBidsByUser, getBidsSummaryByUserOwned,
    } from "@/api/properties"
import { getFavourites, addFavourite } from "@/api/users" 
import type { PropertySearchDTO, PropertyCreateDTO, PropertyUpdateDTO, CreateReservationDTO, CreateBidDTO } from "@/types/Properties"
import { create } from "domain"
// 👇 importa anche il dettaglio
import type { PropertyDetailDTO } from "@/types/Properties"
import { httpProperty } from "@/api/http"   // <-- se non c'è, importa il tuo client axios



export const usePropertyStore = defineStore("properties", {
  state: () => ({
    list: [] as PropertySearchDTO[],
    loading: false as boolean,
    error: null as string | null,



    favList: [] as PropertySearchDTO[],
    favLoading: false as boolean,
    favError: null as string | null,
    favPage: 1,
    favLimit: 20,
  }),



  actions: {
async fetchList(filters?: { 
  city?: string
  minArea?: number | null
  maxPrice?: number | null
  listingType?: string       // nuovo filtro: vendita/affitto
  rooms?: number | null      // nuovo filtro: numero di stanze
  energyClass?: string       // nuovo filtro: classe energetica
}) {
  this.loading = true       //  attivo lo stato di caricamento (spinner o messaggio)
  this.error = null         // resetto eventuali errori precedenti

  try {
    const params: Record<string, any> = {}   // oggetto che conterrà i filtri validi

    //  se la città è stata passata e non è stringa vuota, aggiungila
    if (filters?.city?.trim()) params.city = filters.city.trim()

    //  se la superficie minima è > 0, aggiungila
    if (filters?.minArea != null && filters.minArea > 0) params.minArea = filters.minArea

    //  se il prezzo massimo è > 0, aggiungilo
    if (filters?.maxPrice != null && filters.maxPrice > 0) params.maxPrice = filters.maxPrice

    //  se è stato selezionato un tipo di annuncio (vendita/affitto)
    if (filters?.listingType) params.listingType = filters.listingType

    //  se il numero di stanze è valido (> 0), aggiungilo
    if (filters?.rooms != null && filters.rooms > 0) params.rooms = filters.rooms

    //  se la classe energetica è selezionata (es. "A", "B", "C")
    if (filters?.energyClass) params.energyClass = filters.energyClass

    // chiamata API al backend con i parametri costruiti sopra
    const res = await searchProperties(params)

    //  salvo i risultati nella lista del Pinia store
    this.list = res.data
  } catch (err) {
    console.error("Errore nel caricamento proprietà", err)
    this.error = "Errore nel caricamento delle proprietà"
    this.list = []          // 🔴 se fallisce → svuoto la lista
  } finally {
    this.loading = false    // 🔵 disattivo stato di caricamento
  }

},



    // 🔹 Fetch dettaglio proprietà
    async fetchDetail(id: number): Promise<PropertyDetailDTO> {
      const { data } = await httpProperty.get<PropertyDetailDTO>(`/properties/${id}`)
      return data
    },

    async fetchFavList() {
      this.favLoading = true
      this.favError = null
      try {
        const res = await getFavourites()
        const data = res.data
        const isValid =
          Array.isArray(data) &&
          data.every((x) => x && typeof x === "object" && "id" in x && "title" in x && "city" in x && "area" in x && "price" in x)

    if (!isValid) {
      this.favError = 'Formato risposta non valido dai preferiti'
      this.favList = []
      return
    }
    this.favList = data
  } catch (e:any) {
    if (e.response &&e.response.status === 401){ 
      this.favError = "401"
      this.favList = []
    }else{
      this.favError = e?.message ?? 'Errore nel caricamento dei preferiti'
      this.favList = []
    }
  } finally {
    this.favLoading = false
  }
},

//azione per la ricerca tramite mappa
async fetchListByBounds(lat: number, lon: number, radiusKm: number) {
  this.loading = true
  this.error = null
  try {
    const res = await httpProperty.get("/properties/search-by-bounds", {
      params: { lat, lon, radiusKm }
    })
    this.list = res.data
  } catch (err) {
    console.error("Errore nella ricerca geografica", err)
    this.error = "Errore nella ricerca geografica"
    this.list = []
  } finally {
    this.loading = false
  }
},



    async addToFavourites(idProp: number) {
      try {
        await addFavourite({ idProp })
        const justAdded = this.list.find((p) => p.id === idProp)
        if (justAdded && !this.favList.some((p) => p.id === idProp)) {
          this.favList = [justAdded, ...this.favList]
        }
      } catch (e) {
        console.error("Errore aggiunta preferito", e)
        this.favError = "Errore nell'aggiunta ai preferiti"
      }
    },


  // -------------------------------------------------
  // OPERAZIONI SU RESERVATIONS
  // -------------------------------------------------


  async createAReservation(payload: CreateReservationDTO) {
    try {
      await createReservation(payload)
      
    }catch (e) {
      console.error("Errore creazione prenotazione", e)
    }
  },

  async fetchReservationsByProperty(idProp: number) {
    try {
      const res = await getReservationsByProperty(idProp)
      console.log("Prenotazioni per proprietà", res.data)
      return res.data
    } catch (e) {
      console.error("Errore nel recupero delle prenotazioni per proprietà", e)
      return null
    }
  },
  
  async fetchReservationsByUser() {
    try {
      const res = await getReservationsByUser()
      console.log("Prenotazioni per utente", res.data)
      return res.data
    } catch (e) {
      console.error("Errore nel recupero delle prenotazioni per utente", e)
      return null
    }
  },

    //-------------------------------------------------
    // OPERAZIONI SU BIDS
    //-------------------------------------------------

    async createABid(payload: CreateBidDTO) {
      try {
        const res = await createBid(payload)
        console.log("Offerta creata:", res) 
        return res
      }catch (e) {
        console.error("Errore creazione offerta", e)
        return null
      }
    },

    async fetchBidsByProperty(idProp: number) {
      try {
        const res = await getBidsByProperty(idProp)
        console.log("Offerte per proprietà", res.data)
        return res.data
      } catch (e) {
        console.error("Errore nel recupero delle offerte per proprietà", e)
        return null
      }
    },
    
    async fetchBidsByUser() {
      try {
        const res = await getBidsByUser()
        console.log("Offerte per utente", res.data)
        return res.data
      } catch (e) {
        console.error("Errore nel recupero delle offerte per utente", e)
        return null
      }
    },    

    async fetchBidsSummaryByUserOwned() {
      try {
        const res = await getBidsSummaryByUserOwned()
        console.log("Riepilogo offerte per utente proprietario", res)
        return res.data
      } catch (e) {
        console.error("Errore nel recupero del riepilogo delle offerte per utente proprietario", e)
        return null
      }
    },

    // ✍️ CRUD PROPERTY
  async addProperty(payload: PropertyCreateDTO) {
  try {
   await createProperty(payload)
    //this.list.push(res)
 await this.fetchList
  } catch (err) {
    console.error("Errore addProperty", err)
    throw err
  }
},

    async editProperty(id: number, payload: PropertyUpdateDTO) {
      await updateProperty(id, payload)
      await this.fetchList()
    },
    async removeProperty(id: number) {
      await deleteProperty(id)
      await this.fetchList()
    },
  },
})
