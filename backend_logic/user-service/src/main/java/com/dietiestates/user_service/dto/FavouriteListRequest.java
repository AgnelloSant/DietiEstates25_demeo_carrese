package com.dietiestates.user_service.dto;

import org.bouncycastle.pqc.jcajce.provider.lms.LMSSignatureSpi.generic;

public class FavouriteListRequest {
    
    private Long idUser;
    private Long idProp;

    public FavouriteListRequest(Long idUser, Long idProp) {
        this.idUser = idUser;
        this.idProp = idProp;
        
    }

    public Long getIdUser() {
        return idUser;
    }

    public Long getIdProp() {
        return idProp;
    }

   
}
