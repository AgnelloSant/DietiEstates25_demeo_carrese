package com.dietiestates.property_service.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PropertyCreateDTO {
private String title;
    private String city;
    private Double area;
    private Double price;
    private LocalDate publishedAt;
    private Double latitude;
    private Double longitude;
    private Boolean nearSchool;
    private Boolean nearPark;
    private Boolean nearTransport;
    private String listingType;   // vendita | affitto
    private Integer rooms;
    private String energyClass;
        private String address;
     private Long idUser;      // ID dell'utente che crea la proprietà
    private Long views;
  

    
}