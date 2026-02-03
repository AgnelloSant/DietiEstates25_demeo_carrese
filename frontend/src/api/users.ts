import { httpProperty, httpUS } from './http'
import type { AxiosResponse } from 'axios'
import type { PropertySearchDTO } from '@/types/Properties'

export async function getFavourites(): Promise<AxiosResponse<PropertySearchDTO[]>> {
  const token = localStorage.getItem("token")
  // Path changed to /properties/favourites/get
  const res = await httpProperty.get<PropertySearchDTO[]>('/favourites/get', {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
  return res
}


export const addFavourite = (payload: { idProp: number }) => {
  const token = localStorage.getItem("token")

  // Path changed to /properties/favourites/add, payload body structure matches backend map expectation
  return httpProperty.post<void>("/favourites/add", payload, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
}

export const removeFavourite = (idProp: number) => {
  const token = localStorage.getItem("token")

  return httpProperty.delete<void>(`/favourites/remove/${idProp}`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
}
