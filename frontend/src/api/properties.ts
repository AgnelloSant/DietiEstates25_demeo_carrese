// src/api/properties.ts
// Wrapper per chiamate al microservizio property-service

import { httpProperty } from "./http";
import type { PropertySearchDTO, PropertyCreateDTO, PropertyUpdateDTO } from "@/types/Properties";
import type { AxiosResponse } from "axios";

/*
 * 🔎 Ricerca proprietà
 * GET → /property-service/api/properties/search
 * con query params opzionali (city, minArea, maxPrice)
 */
export const searchProperties = (params?: {
  city?: string;
  minArea?: number;
  maxPrice?: number;
}): Promise<AxiosResponse<PropertySearchDTO[]>> => {
  return httpProperty.get<PropertySearchDTO[]>("/properties/search", { params });
};

/*
 * ➕ Creazione nuova proprietà
 * POST → /property-service/api/properties/create
 */
export async function createProperty(payload: PropertyCreateDTO) {
  const { data } = await httpProperty.post<PropertyCreateDTO>("/properties/create", payload);
  return data;
}

/*
 * ✏️ Aggiornamento proprietà
 * PUT → /property-service/api/properties/update/{id}
 */
export async function updateProperty(id: number, payload: PropertyUpdateDTO) {
  const { data } = await httpProperty.put(`/properties/update/${id}`, payload);
  return data;
}

/*
 * 🗑️ Eliminazione proprietà
 * DELETE → /property-service/api/properties/delete/{id}
 */
export async function deleteProperty(id: number) {
  await httpProperty.delete(`/properties/delete/${id}`);
}
