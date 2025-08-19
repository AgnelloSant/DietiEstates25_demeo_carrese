package com.dietiestates.property_service.dto;

import java.time.LocalDate;

public class PropertyCreateDTO {
    private String title;
    private String city;
    private Double area;
    private Double price;
 private LocalDate publishedAt;

    // Costruttori
    public PropertyCreateDTO() {}

    public PropertyCreateDTO(String title, String city, Double area, Double price,LocalDate publishedAt) {
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
        this.publishedAt=publishedAt;
    }

    // Getter e Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDate getpublishedAt(){return publishedAt; }
    public void setpublishedAt(LocalDate publishedAt) { this.publishedAt = publishedAt;}
}