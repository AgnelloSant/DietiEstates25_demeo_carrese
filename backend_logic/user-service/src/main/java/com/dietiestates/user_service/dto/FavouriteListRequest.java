package com.dietiestates.user_service.dto;


public class FavouriteListRequest {
    
    private Long idProp;

    public FavouriteListRequest(Long idUser, Long idProp) {
        this.idProp = idProp;
        
    }

    public Long getIdProp() {
        return idProp;
    }

   
}
