import { httpUS } from './http'
import type { AxiosResponse } from 'axios'
import type { PropertySearchDTO } from '@/types/Properties'

export async function getFavourites (): Promise<AxiosResponse<PropertySearchDTO[]>> {
  const token = localStorage.getItem("token")
  const res = await httpUS.get<PropertySearchDTO[]>('/favourites', {
    headers: { 
      Authorization: `Bearer ${token}`
    }
  })
  return res
}


export const addFavourite = (payload: { idProp:number }) => { 
  const token = localStorage.getItem("token")

  return httpUS.post<void>("/addfavourite", payload, { 
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
}
