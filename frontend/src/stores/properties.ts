// src/stores/properties.ts
// Store Pinia per gestire le proprietà immobiliari

import { defineStore } from "pinia"
import {
  searchProperties,
  createProperty,
  updateProperty,
  deleteProperty,
} from "@/api/properties"

// Import dei tipi dal file giusto (Properties.ts con P maiuscola)
import type {
  PropertySearchDTO,
  PropertyCreateDTO,
  PropertyUpdateDTO,
} from "@/types/Properties"

export const usePropertyStore = defineStore("properties", {
  state: () => ({
    list: [] as PropertySearchDTO[], // elenco immobili
    loading: false as boolean,       // stato di caricamento
    error: null as string | null,    // eventuale messaggio di errore
  }),

  actions: {
    // 🔹 Carica la lista di immobili dal backend
    async fetchList(filters?: { city?: string; minArea?: number; maxPrice?: number }) {
      this.loading = true
      this.error = null
      try {
        // searchProperties ritorna AxiosResponse ⇒ dobbiamo prendere solo .data
        const res = await searchProperties(filters)
        this.list = res.data // ✅ fix errore TS2740
      } catch (err) {
        console.error("Errore nel caricamento proprietà", err)
        this.error = "Errore nel caricamento delle proprietà"
        this.list = [] // fallback
      } finally {
        this.loading = false
      }
    },

    // 🔹 Crea nuova proprietà
    async addProperty(payload: PropertyCreateDTO) {
      await createProperty(payload)
      await this.fetchList() // ricarica la lista
    },

    // 🔹 Modifica proprietà
    async editProperty(id: number, payload: PropertyUpdateDTO) {
      await updateProperty(id, payload)
      await this.fetchList()
    },

    // 🔹 Elimina proprietà
    async removeProperty(id: number) {
      await deleteProperty(id)
      await this.fetchList()
    },
  },
})
