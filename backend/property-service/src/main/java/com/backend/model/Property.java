package com.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;
    private String city;
    private Double price;

    @Column(nullable = false, name = "area_mq")
    private Double area;

    @Column(name = "published_at")
    private LocalDate publishedAt;

    private Double latitude;
    private Double longitude;

    private boolean nearSchool;
    private boolean nearPark;
    private boolean nearTransport;

    private String listingType;
    private Integer rooms;
    private String energyClass;

    @Column(name = "address")
    private String address;

    @Column(name = "user_id", nullable = false)
    private Long idUser;

    private Long views;
}
