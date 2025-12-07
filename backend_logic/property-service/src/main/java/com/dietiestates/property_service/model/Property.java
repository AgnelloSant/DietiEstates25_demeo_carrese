package com.dietiestates.property_service.model;


import jakarta.persistence.*;             // per JPA
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;              // per la data di pubblicazione



@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "properties") // nome tabella nel database
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incrementale
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;
    private String city;
    private Double price;

    @Column(nullable = false, name = "area_mq")  // "mq" = metri quadrati
    private Double area; 

    @Column(name = "published_at")
    private LocalDate publishedAt;


//coordinate per geoapify
private Double latitude;
private Double longitude;

    private boolean nearSchool;
    private boolean nearPark;
    private boolean nearTransport;

    //attributi per ricerca avanzata
      private String listingType;   // "vendita" | "affitto"
    private Integer rooms;        // numero stanze
    private String energyClass;   // es: A, B, C, ...

    @Column(name = "address")
private String address;

   @Column(name = "user_id", nullable = false)
    private Long idUser;

    private Long views; 
     




    
}
