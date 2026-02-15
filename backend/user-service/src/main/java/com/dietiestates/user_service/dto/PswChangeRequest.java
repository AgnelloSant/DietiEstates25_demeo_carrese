package com.dietiestates.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PswChangeRequest {
    private String oldPsw;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    private String newPsw;

    @NotBlank(message = "Email is required")
    private String email;

    public PswChangeRequest() {
    }

    public PswChangeRequest(String oldPsw, String newPsw, String email) {
        this.oldPsw = oldPsw;
        this.newPsw = newPsw;
        this.email = email;
    }

    public PswChangeRequest(String oldPsw, String newPsw) {
        this.oldPsw = oldPsw;
        this.newPsw = newPsw;
    }

    public String getOldPsw() {
        return oldPsw;
    }

    public void setOldPsw(String oldPsw) {
        this.oldPsw = oldPsw;
    }

    public String getNewPsw() {
        return newPsw;
    }

    public void setNewPsw(String newPsw) {
        this.newPsw = newPsw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
