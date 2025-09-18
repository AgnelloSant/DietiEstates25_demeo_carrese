// src/api/properties.ts
import { httpProperty } from "./http";
import type { PropertySearchDTO, PropertyCreateDTO, PropertyUpdateDTO } from "@/types/Properties";
import type { AxiosResponse } from "axios";

// 🔍 Ricerca
export const searchProperties = (params?: {
  city?: string
  minArea?: number
  maxPrice?: number
  listingType?: string     // "vendita" | "affitto"
  rooms?: number
  energyClass?: string
}): Promise<AxiosResponse<PropertySearchDTO[]>> => {
  return httpProperty.get<PropertySearchDTO[]>("/properties/search", { params })
}

// ✍️ Creazione proprietà
export async function createProperty(payload: PropertyCreateDTO) {
  // POST /properties/create
  const { data } = await httpProperty.post<PropertyCreateDTO>('/properties/create', payload)
  return data
}


// ✏️ Update
export async function updateProperty(id: number, payload: PropertyUpdateDTO) {
  const { data } = await httpProperty.put(
    `/properties/update/${id}`,
    payload,
    { headers: { "Content-Type": "application/json" } }
  )
  return data
}

// ❌ Delete
export async function deleteProperty(id: number) {
  await httpProperty.delete(`/properties/delete/${id}`)
}
