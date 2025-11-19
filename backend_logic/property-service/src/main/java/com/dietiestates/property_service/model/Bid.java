package com.dietiestates.property_service.model;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter 
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

   

}
