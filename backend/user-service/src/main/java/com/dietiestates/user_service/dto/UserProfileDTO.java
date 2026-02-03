package com.dietiestates.user_service.dto;

public class UserProfileDTO {
    private String name;
    private String email;
    private String phone;
    private String role;
    private String provider;

    public UserProfileDTO(String name, String email, String phone, String role, String provider) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

}