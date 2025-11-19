// src/api/properties.ts
import { httpProperty } from "./http";
import { type PropertySearchDTO, type PropertyCreateDTO, type PropertyUpdateDTO, 
  type CreateReservationDTO, type CreateBidDTO, 
  BidSummaryDTO} from "@/types/Properties";
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


/*
  * 📅 Recupera prenotazioni per una proprietà
  * GET → /property-service/api/properties/getReservations/property
  * GET → /property-service/api/properties/getReservations/user
  */
  export async function getReservationsByProperty(id: number) {
    const { data } = await httpProperty.get(`/properties/reservations/getbyproperty/${id}`);
    return data;
  }

  export async function getReservationsByUser() {
    const { data } = await httpProperty.get(`/properties/reservations/getbyuser/me`);
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
    // var propertyBids = await httpProperty.get(`/properties/bids/countByProperty/${id}`);
      const data = {
        bookings: propertyBookings.data,
       // bids: propertyBids.data
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

export async function getBidsByUser() {
  const { data } = await httpProperty.get(`/properties/bids/getbyuser`);
  return data;
}

export async function getBidsSummaryByUserOwned() {
  const { data } = await httpProperty.get(`/properties/bids/getsummary`);
  return data;
}

export async function getMonthlyTrend() {
  const { data } = await httpProperty.get(`/properties/bids/getmonthlytrend`); 
  return data; 
}

/*
  * 💸 Crea una nuova offerta
  * POST → /property-service/api/properties/newBid/{id}
  */
 export async function createBid(payload: CreateBidDTO) {
  const token = localStorage.getItem("token"); 

  const { data } = await httpProperty.post<boolean>(
    `/properties/bids/new`, 
    payload,
    { 
      headers:{ Authorization: `Bearer ${token}`,
        },
    }
   );
  return data;
}
