// src/api/properties.ts
import { httpProperty } from "./http";
import {
  type PropertySearchDTO, type PropertyCreateDTO, type PropertyUpdateDTO,
  type CreateReservationDTO, type CreateBidDTO,
  BidSummaryDTO, type PropertyDetailDTO
} from "@/types/Properties";
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
  return httpProperty.get<PropertySearchDTO[]>("/search", { params })
}

export const searchByBounds = (params: {
  lat: number
  lon: number
  radiusKm: number
}): Promise<AxiosResponse<PropertySearchDTO[]>> => {
  return httpProperty.get<PropertySearchDTO[]>("/search/bybounds", { params })
}

export async function getPropertyDetail(id: number) {
  const { data } = await httpProperty.get<PropertyDetailDTO>(`/${id}`)
  return data
}


export async function uploadPropertyImage(id: number, file: File) {
  const formData = new FormData()
  formData.append("file", file)
  await httpProperty.post(`/${id}/upload`, formData, {
    headers: {
      "Content-Type": "multipart/form-data"
    }
  })
}

export async function getPropertyImages(id: number): Promise<string[]> {
  const { data } = await httpProperty.get<string[]>(`/${id}/images`)
  return data
}

// Creazione proprietà
export async function createProperty(payload: PropertyCreateDTO) {
  // POST /create
  const { data } = await httpProperty.post<PropertyCreateDTO>('/create', payload)
  return data
}


// Update
export async function updateProperty(id: number, payload: PropertyUpdateDTO) {
  const { data } = await httpProperty.put(
    `/update/${id}`,
    payload,
    { headers: { "Content-Type": "application/json" } }
  )
  return data
}

export async function deleteProperty(id: number) {
  await httpProperty.delete(`/delete/${id}`)
}


/*
  * Recupera prenotazioni per una proprietà
  * GET → /reservations/getbyproperty/{id}
  */
export async function getReservationsByProperty(id: number) {
  const { data } = await httpProperty.get(`/reservations/getbyproperty/${id}`);
  return data;
}

export async function getReservationsByUser() {
  const token = localStorage.getItem("token");
  const { data } = await httpProperty.get(`/reservations/getbyuser`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return data;
}

/*
* Crea una nuova prenotazione
* POST → /reservations/new
*/
export async function createReservation(payload: CreateReservationDTO) {
  const { data } = await httpProperty.post<CreateReservationDTO>(`/reservations/new`, payload);
  return data;
}

export async function getStats(idProp: number) {
  var propertyBookings = await httpProperty.get(`/reservations/countbyproperty/${idProp}`);
  // var propertyBids = await httpProperty.get(`/bids/countByProperty/${id}`);
  const data = {
    bookings: propertyBookings.data,
    // bids: propertyBids.data
  };

  return data;
}

/*
  * 💸 Recupera offerte per una proprietà o un utente
  * GET → /bids/getbyproperty/{id}
  * GET → /bids/getbyuser/{id}
  */
export async function getBidsByProperty(idProp: number) {
  const { data } = await httpProperty.get(`/bids/getbyproperty/${idProp}`);
  return data;
}

export async function getBidsByUser() {
  const token = localStorage.getItem("token");
  const { data } = await httpProperty.get(`/bids/getbyuser`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return data;
}

export async function getBidsSummaryByUserOwned() {
  const token = localStorage.getItem("token");
  const { data } = await httpProperty.get(`/bids/getsummary`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return data;
}

export async function getMonthlyTrend() {
  const token = localStorage.getItem("token");
  const { data } = await httpProperty.get(`/bids/getmonthlytrend`, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return data;
}

export async function createBid(payload: CreateBidDTO) {
  const token = localStorage.getItem("token");

  const { data } = await httpProperty.post<boolean>(
    `/bids/new`,
    payload,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  return data;
}

export const getFavourites = () => {
  return httpProperty.get('/favourites/get')
}

export const addFavourite = (propertyId: number) => {
  return httpProperty.post('/favourites/add', { idProp: propertyId })
}

export const removeFavourite = (propertyId: number) => {
  return httpProperty.delete(`/favourites/remove/${propertyId}`)
}

export async function downloadReservationsExcel() {
  const token = localStorage.getItem("token");
  const response = await httpProperty.get('/reservations/getexcel', {
    headers: { Authorization: `Bearer ${token}` },
    responseType: 'blob', // Important for binary data
  });
  return response;
}

export async function downloadBidsExcel() {
  const token = localStorage.getItem("token");
  const response = await httpProperty.get('/bids/getreceived', {
    headers: { Authorization: `Bearer ${token}` },
    responseType: 'blob',
  });
  return response;
}

