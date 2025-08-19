//Funzioni che incapsulano le chiamate alle tue endpoint Spring.
// src/api/properties.ts
import { http } from "./http";
import type { PropertySearchDTO, PropertyCreateDTO ,PropertyUpdateDTO} from "@/types/Properties";
import type { AxiosResponse } from "axios";

// Ricerca proprietà con filtri opzionali
export const searchProperties = (params?: {
  city?: string;
  minArea?: number;
  maxPrice?: number;
}): Promise<AxiosResponse<PropertySearchDTO[]>> => {
  return http.get<PropertySearchDTO[]>("/properties/search", { params });
};

// 2) Creazione proprietà
export async function createProperty(payload: PropertyCreateDTO) {
  // POST /api/properties/create
  const { data } = await http.post<PropertyCreateDTO>('/properties/create', payload)
  return data
}

// 3) Aggiornamento proprietà
export async function updateProperty(id: number, payload: PropertyUpdateDTO) {
  // PUT /api/properties/update/{id}
  const { data } = await http.put(`/properties/update/${id}`, payload)
  return data
}

// 4) Cancellazione proprietà
export async function deleteProperty(id: number) {
  // DELETE /api/properties/delete/{id}
  await http.delete(`/properties/delete/${id}`)
}