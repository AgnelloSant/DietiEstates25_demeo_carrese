//Servono come documentazione del shape dei dati attesi dal backend. Se usi JS, tienili come riferimento.
// Modella i DTO in base a quanto esposto dal tuo backend
// PropertySearchDTO (risultati della ricerca)

import { RouteLocationNormalizedLoadedTyped } from "vue-router"

// src/types/Properties.ts
export interface PropertySearchDTO {
  id: number
  title: string
  city: string
  area: number
  price: number
  imageUrl?: string 

  nearSchool?: boolean
  nearPark?: boolean
  nearTransport?: boolean

  listingType?: string      // vendita / affitto
  rooms?: number
  energyClass?: string
    latitude?: number | null
  longitude?: number | null
  address?: string
}


// PropertyCreateDTO
export interface PropertyCreateDTO {
  title: string
  city: string
   address: string
  area: number
  price: number
  latitude?: number | null
  longitude?: number | null
  nearSchool?: boolean
  nearPark?: boolean
  nearTransport?: boolean
  listingType: "vendita" | "affitto"   // tipologia inserzione
  rooms: number                        // numero stanze
  energyClass: string                  // es. A, B, C...

  //TODO rivedere logica creazione nuovo annuncio
     idUser: number        // Nuovo campo obbligatorio
    views?: number 
}

// PropertyUpdateDTO
export interface PropertyUpdateDTO {
  title: string
  city: string
   address: string
  area: number
  price: number
  latitude?: number | null
  longitude?: number | null
  nearSchool?: boolean
  nearPark?: boolean
  nearTransport?: boolean
  listingType: "vendita" | "affitto"   // tipologia inserzione
  rooms: number                        // numero stanze
  energyClass: string                  // es. A, B, C...
     idUser: number        // Nuovo campo obbligatorio
    views?: number 
}

// PropertyDetailDTO
export interface PropertyDetailDTO {
  id: number
  title: string
  city: string
   address: string
  area: number
  price: number
  description?: string
  publishedAt?: string
  imageUrl?: string 
  latitude?: number | null
  longitude?: number | null
  nearSchool?: boolean
  nearPark?: boolean
  nearTransport?: boolean
  listingType?: "vendita" | "affitto"
  rooms?: number
  energyClass?: string

  //TODO: Rivedere uso dell'id
    idUser?: number        // Nuovo campo obbligatorio
    views?: number 
}

export interface FavoriteDTO {
  id_user: number
  id_prop: number
  created_at?: string
}

export interface CreateReservationDTO {
  id_prop: number
  date: string
  time: string
}

export interface CreateBidDTO {
  id_prop: number
  amount: number
}

export interface BidSummaryDTO{
  id_prop: number
  propTitle: string
  count: number
  avgPrice: number
  lastDate: Date
}

export interface BidDailyCount {  
  dateLabel: string;
  offerCount: number;
}

