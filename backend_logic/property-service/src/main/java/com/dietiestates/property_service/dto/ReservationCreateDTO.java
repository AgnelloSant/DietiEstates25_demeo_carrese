package com.dietiestates.property_service.dto;

public class ReservationCreateDTO {
    private Long idProp;
    private Long idUser;
    private String date;

    public ReservationCreateDTO() {}

    public ReservationCreateDTO(Long idProp, Long idUser, String date) {
        this.idProp = idProp;
        this.idUser = idUser;
        this.date = date;
    }

    public Long getIdProp() {
        return idProp;
    }

    public void setIdProp(Long idProp) {
        this.idProp = idProp;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
