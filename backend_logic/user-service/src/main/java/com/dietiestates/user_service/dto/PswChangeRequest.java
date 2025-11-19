package com.dietiestates.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class PswChangeRequest {
    private String oldPsw; 
    private String newPsw; 
    private String email; 

    public PswChangeRequest(String oldPsw, String newPsw, String email) {
        this.oldPsw = oldPsw;
        this.newPsw = newPsw;
        this.email = email;
    }

    public PswChangeRequest(String oldPsw, String newPsw) {
        this.oldPsw = oldPsw;
        this.newPsw = newPsw;
    }
    
    public PswChangeRequest(){ 

    }

  
    
}
