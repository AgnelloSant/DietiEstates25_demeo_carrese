package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDetailDTO {

    public PropertyDetailDTO(Long id, String title, String city, Double area, Double price) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
    }

    public PropertyDetailDTO(Long id, String title, String city, Double area, Double price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private Long id;
    private String title;
    private String city;
    private Double area;
    private Double price;

    private boolean nearSchool;
    private boolean nearPark;
    private boolean nearTransport;

    private String description;
    private LocalDate publishedAt;
    private String listingType;
    private Integer rooms;
    private String energyClass;
    private String address;
    private Long idUser;
    private Long views;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
}
