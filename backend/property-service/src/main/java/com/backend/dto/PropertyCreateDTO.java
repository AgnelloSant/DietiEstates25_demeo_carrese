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
public class PropertyCreateDTO {
    private String title;
    private String description;
    private String city;
    private Double area;
    private Double price;
    private LocalDate publishedAt;
    private Double latitude;
    private Double longitude;
    private Boolean nearSchool;
    private Boolean nearPark;
    private Boolean nearTransport;
    private String listingType;
    private Integer rooms;
    private String energyClass;
    private String address;
    private Long idUser;
    private Long views;
}