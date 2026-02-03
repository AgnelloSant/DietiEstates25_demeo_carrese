package com.dietiestates.user_service.dto;

public class PswChangeRequest {
    private String oldPsw;
    private String newPsw;
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
