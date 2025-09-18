//Servono come documentazione del shape dei dati attesi dal backend. Se usi JS, tienili come riferimento.
// Modella i DTO in base a quanto esposto dal tuo backend
// PropertySearchDTO (risultati della ricerca)
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
}

// PropertyUpdateDTO
export interface PropertyUpdateDTO {
  title?: string
  city?: string
  area?: number
  price?: number
   address: string
  listingType?: "vendita" | "affitto"
  rooms?: number
  energyClass?: string
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
}

export interface FavoriteDTO {
  id_user: number
  id_prop: number
  created_at?: string
}

export interface CreateReservationDTO {
  id_prop: number
  id_user: number
  date: string
}

export interface CreateBidDTO {
  id_prop: number
  id_user: number
  amount: number
}

