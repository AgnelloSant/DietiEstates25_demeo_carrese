package com.dietiestates.shared.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor 
public class PropertySearchDTO {
    private Long id;
    private String title;
    private String city;
    private Double area;
    private Double price;
    private boolean nearSchool;    // per geoapify
    private boolean nearPark;
    private boolean nearTransport;

//ricerca avanzata
   private String listingType;
    private Integer rooms;
    private String energyClass;
private String address;
    private Double latitude;
    private Double longitude;

 

    // 👇 Costruttore "vecchio" usato in più punti (senza i campi geoapify)
    public PropertySearchDTO(Long id, String title, String city, Double area, Double price) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
    }

    // 👇 Costruttore "nuovo" con anche i campi geoapify e ricerca avanzata
    
    public PropertySearchDTO(Long id, String title, String city, Double area, Double price,
        boolean nearSchool, boolean nearPark, boolean nearTransport, String listingType, Integer rooms,
        String energyClass,String address,Double latitude,Double longitude) {
    this.id = id;
    this.title = title;
    this.city = city;
    this.area = area;
    this.price = price;
    this.nearSchool = nearSchool;
    this.nearPark = nearPark;
    this.nearTransport = nearTransport;
    this.listingType = listingType;
    this.rooms = rooms;
    this.energyClass = energyClass;
     this.address = address;
     this.latitude=latitude;
     this.longitude=longitude;
}



   
    
}
