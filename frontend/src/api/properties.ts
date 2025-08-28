import { http } from "./http"
import type { AxiosResponse } from "axios"
import type { PropertySearchDTO, PropertyCreateDTO, PropertyUpdateDTO } from "@/types/Properties"

export const searchProperties = (params?: {
  city?: string; minArea?: number; maxPrice?: number;
}): Promise<AxiosResponse<PropertySearchDTO[]>> =>
  http.get<PropertySearchDTO[]>("/properties/search", { params })

export const createProperty = (payload: PropertyCreateDTO) =>
  http.post("/properties/create", payload)

export const updateProperty = (id: number, payload: PropertyUpdateDTO) =>
  http.put(`/properties/update/${id}`, payload)

export const deleteProperty = (id: number) =>
  http.delete(`/properties/delete/${id}`)
