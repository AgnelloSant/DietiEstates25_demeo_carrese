// src/main/java/com/dietiestates/property_service/dto/PropertyDetailDTO.java
package com.dietiestates.property_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PropertyDetailDTO {

    private Long id;
    private String title;
    private String city;
    private Double area;
    private Double price;
    private String description;   // 👈 opzionale, se hai campo in entity
    private String publishedAt;   // 👈 opzionale, se hai LocalDateTime in entity
    private Double latitude;   
    private Double longitude;  
    private boolean nearSchool;    
    private boolean nearPark;      
    private boolean nearTransport;
// Tipo di inserzione: "vendita" o "affitto"
private String listingType;

// Numero stanze
private Integer rooms;

// Classe energetica: es. "A", "B", "C", ...
private String energyClass;

private String address;
   private Long idUser;      // ID dell'utente che crea la proprietà
    private Long views;



    
}
