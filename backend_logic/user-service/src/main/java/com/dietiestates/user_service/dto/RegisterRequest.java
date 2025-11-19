package com.dietiestates.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RegisterRequest {

    public String name;
    public String email;
    public String password;
    public String phone;
    public String role;

   
}
