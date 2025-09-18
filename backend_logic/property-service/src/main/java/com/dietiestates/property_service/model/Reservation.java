package com.dietiestates.property_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    @Column(name = "property_id", nullable = false)
    private Long idProp;
    @Column(name = "user_id", nullable = false)
    private Long idUser; 
    @Column(name = "reservation_date", nullable = false)
    private String date; 

    public Reservation() {
    }
    public Reservation(Long idProp, Long idUser, String date) {
        this.idProp = idProp;
        this.idUser = idUser;
        this.date = date;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
