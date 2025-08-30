import { httpUS } from './http'
import type { AxiosResponse } from 'axios'
import type { PropertySearchDTO } from '@/types/Properties'

// finché non usi JWT, passiamo l'utente via header X-User-Id
export async function getFavourites (
  
): Promise<AxiosResponse<PropertySearchDTO[]>> {
  const res = await httpUS.get<PropertySearchDTO[]>('/favourites', {})
  return res
}


export const addFavourite = (payload: { idUser:number; idProp:number}) =>
  httpUS.post<void>("/addfavourite", payload)

