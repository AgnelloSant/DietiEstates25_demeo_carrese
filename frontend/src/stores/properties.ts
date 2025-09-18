// src/stores/properties.ts
import { defineStore } from "pinia"
import { searchProperties, createProperty, updateProperty, deleteProperty, createReservation, 
      getReservationsByProperty, getReservationsByUser, createBid, getBidsByProperty,
      getBidsByUser, getBidsSummaryByUserOwned
    } from "@/api/properties"
import { getFavourites, addFavourite } from "@/api/users" 
import type { PropertySearchDTO, PropertyCreateDTO, PropertyUpdateDTO, CreateReservationDTO, CreateBidDTO } from "@/types/Properties"
import { create } from "domain"

export const usePropertyStore = defineStore("properties", {
  state: () => ({
    list: [] as PropertySearchDTO[],
    loading: false as boolean,
    error: null as string | null,

    // 🔹 stato per Preferiti
    favList: [] as PropertySearchDTO[],
    favLoading: false as boolean,
    favError: null as string | null,
    favPage: 1,
    favLimit: 20,
  }),

  actions: {
    // -------------------------------------------------
    // LISTA PROPERTIES
    // -------------------------------------------------
    async fetchList(filters?: { city?: string; minArea?: number | null; maxPrice?: number | null }) {
      this.loading = true
      this.error = null
      try {
        const params: Record<string, any> = {}
        if (filters?.city?.trim()) params.city = filters.city.trim()
        if (filters?.minArea != null && filters.minArea > 0) params.minArea = filters.minArea
        if (filters?.maxPrice != null && filters.maxPrice > 0) params.maxPrice = filters.maxPrice
        const res = await searchProperties(params)
        this.list = res.data
      } catch (err) {
        console.error("Errore nel caricamento proprietà", err)
        this.error = "Errore nel caricamento delle proprietà"
        this.list = []
      } finally {
        this.loading = false
      }
    },

    // -------------------------------------------------
    // LISTA PREFERITI (DTO dal user-service → property-service)
    // -------------------------------------------------
 async fetchFavList() {
  // usa i valori dello store se non passati

  this.favLoading = true
  this.favError = null
  try {
    const res = await getFavourites()
    const data = res.data
    const isValid =
      Array.isArray(data) &&
      data.every((x) => x && typeof x === 'object' && 'id' in x && 'title' in x && 'city' in x && 'area' in x && 'price' in x)

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

  async addToFavourites( idProp: number){ 
     try {
    await addFavourite({ idUser: 2, idProp })   // userId fisso per test
    const justAdded = this.list.find(p => p.id === idProp)
    if (justAdded && !this.favList.some(p => p.id === idProp)) {
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
  
  async fetchReservationsByUser(idUser: number) {
    try {
      const res = await getReservationsByUser(idUser)
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
    
    async fetchBidsByUser(idUser: number) {
      try {
        const res = await getBidsByUser(idUser)
        console.log("Offerte per utente", res.data)
        return res.data
      } catch (e) {
        console.error("Errore nel recupero delle offerte per utente", e)
        return null
      }
    },    

    async fetchBidsSummaryByUserOwned(idUser: number) {
      try {
        const res = await getBidsSummaryByUserOwned(idUser)
        console.log("Riepilogo offerte per utente proprietario", res)
        return res.data
      } catch (e) {
        console.error("Errore nel recupero del riepilogo delle offerte per utente proprietario", e)
        return null
      }
    },




    // -------------------------------------------------
    // CRUD PROPERTY
    // -------------------------------------------------
    async addProperty(payload: PropertyCreateDTO) {
      await createProperty(payload)
      await this.fetchList()
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
