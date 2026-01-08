package com.dietiestates.user_service.dto;

public class FavouriteListRequest {
    
    private Long idProp;
    private long idUser;

    public FavouriteListRequest(Long idUser, Long idProp) {
        this.idProp = idProp;
        
    }

    public FavouriteListRequest() {
    }

    public Long getIdProp() {
        return idProp;
    }

    public void setIdProp(Long idProp) {
        this.idProp = idProp;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

   
}
