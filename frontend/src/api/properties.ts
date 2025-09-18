// src/api/properties.ts
// Wrapper per chiamate al microservizio property-service

import { httpProperty } from "./http";
import type { PropertySearchDTO, PropertyCreateDTO, PropertyUpdateDTO, 
  CreateReservationDTO, CreateBidDTO } from "@/types/Properties";
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

/*
  * 📅 Recupera prenotazioni per una proprietà
  * GET → /property-service/api/properties/getReservations/property/{id}
  * GET → /property-service/api/properties/getReservations/user/{id}
  */
  export async function getReservationsByProperty(id: number) {
    const { data } = await httpProperty.get(`/properties/reservations/getbyproperty/${id}`);
    return data;
  }

  export async function getReservationsByUser(id: number) {
    const { data } = await httpProperty.get(`/properties/reservations/getbyuser/${id}`);
    return data;
  }

  /*
  * 📅 Crea una nuova prenotazione
  * POST → /property-service/api/properties/newReservation/{id}
  */
  export async function createReservation(payload: CreateReservationDTO) {
    const { data } = await httpProperty.post<CreateReservationDTO>(`/properties/reservations/new`, payload);
    return data;
  }

  export async function getStats(id: number) {
     var propertyBookings = await httpProperty.get(`/properties/reservations/countByProperty/${id}`);
    var propertyBids = await httpProperty.get(`/properties/bids/countByProperty/${id}`);
      const data = {
        bookings: propertyBookings.data,
        bids: propertyBids.data
      };

    return data;
  }

/*
  * 💸 Recupera offerte per una proprietà o un utente
  * GET → /property-service/api/properties/getBids/property/{id}
  * GET → /property-service/api/properties/getBids/user/{id}
  */
 export async function getBidsByProperty(id: number) {
  const { data } = await httpProperty.get(`/properties/bids/getbyproperty/${id}`);
  return data;
}

export async function getBidsByUser(id: number) {
  const { data } = await httpProperty.get(`/properties/bids/getbyuser/${id}`);
  return data;
}

export async function getBidsSummaryByUserOwned(idUser: number) {
  const { data } = await httpProperty.get(`/properties/bids/getsummary/${idUser}`);
  return data;
}

/*
  * 💸 Crea una nuova offerta
  * POST → /property-service/api/properties/newBid/{id}
  */
 export async function createBid(payload: CreateBidDTO) {
  const { data } = await httpProperty.post<boolean>(`/properties/bids/new`, payload);
  return data;
}
