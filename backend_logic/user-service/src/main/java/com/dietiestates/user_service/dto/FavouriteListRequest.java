package com.dietiestates.user_service.dto;

import org.bouncycastle.pqc.jcajce.provider.lms.LMSSignatureSpi.generic;

public class FavouriteListRequest {
    
    private Long idProp;

    public FavouriteListRequest(Long idUser, Long idProp) {
        this.idProp = idProp;
        
    }

    public Long getIdProp() {
        return idProp;
    }

   
}
