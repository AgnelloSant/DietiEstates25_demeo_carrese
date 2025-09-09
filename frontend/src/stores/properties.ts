// src/stores/properties.ts
import { defineStore } from "pinia"
import { searchProperties, createProperty, updateProperty, deleteProperty } from "@/api/properties"
import { getFavourites, addFavourite } from "@/api/users" 
import type { PropertySearchDTO, PropertyCreateDTO, PropertyUpdateDTO } from "@/types/Properties"
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
          this.favError = "Formato risposta non valido dai preferiti"
          this.favList = []
          return
        }
        this.favList = data
      } catch (e: any) {
        this.favError = e?.message ?? "Errore nel caricamento dei preferiti"
        this.favList = []
      } finally {
        this.favLoading = false
      }
    },

    async addToFavourites(idProp: number) {
      try {
        await addFavourite({ idUser: 2, idProp })
        const justAdded = this.list.find((p) => p.id === idProp)
        if (justAdded && !this.favList.some((p) => p.id === idProp)) {
          this.favList = [justAdded, ...this.favList]
        }
      } catch (e) {
        console.error("Errore aggiunta preferito", e)
        this.favError = "Errore nell'aggiunta ai preferiti"
      }
    },

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
