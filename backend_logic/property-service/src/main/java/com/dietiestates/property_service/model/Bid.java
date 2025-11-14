package com.dietiestates.property_service.model;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    private Long id;
    @Column(name = "property_id", nullable = false)
    private Long propertyId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    private Double amount;
    private LocalDateTime publishedAt; 

    public Bid() {
    }

    public Bid(Long id, Long idProperty, Long idUser, Double amount, LocalDateTime publishedAt) {
        this.id = id;
        propertyId = idProperty;
        userId = idUser;
        this.amount = amount;
        this.publishedAt = publishedAt;
    }  
    
    public Bid(Long idProperty, Long idUser, Double amount) {
        propertyId = idProperty;
        userId = idUser;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getIdProperty() {
        return propertyId;
    }
    public void setIdProperty(Long idProperty) {
        propertyId = idProperty;
    }
    public Long getIdUser() {
        return userId;
    }
    public void setIdUser(Long idUser) {
        userId = idUser;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }



}
