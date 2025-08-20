//Servono come documentazione del shape dei dati attesi dal backend. Se usi JS, tienili come riferimento.
// Modella i DTO in base a quanto esposto dal tuo backend
// PropertySearchDTO (risultati della ricerca)
// src/types/Properties.ts
export interface PropertySearchDTO {
  id: number;   
  title: string;
  city: string;
  area: number;
  price: number;
    imageUrl?: string ;
}


// PropertyCreateDTO
export interface PropertyCreateDTO {
  title: string
  city: string
  area: number
  price: number
}

// PropertyUpdateDTO
export interface PropertyUpdateDTO {
  title?: string
  city?: string
  area?: number
  price?: number
}

export interface PropertyDetailDTO {
  id: number
  title: string
  city: string
  area: number
  price: number
  description?: string
  publishedAt?: string
  imageUrl?: string ;
}