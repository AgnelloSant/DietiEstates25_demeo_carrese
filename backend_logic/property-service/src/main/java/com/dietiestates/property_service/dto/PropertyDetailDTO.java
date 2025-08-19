// src/main/java/com/dietiestates/property_service/dto/PropertyDetailDTO.java
package com.dietiestates.property_service.dto;

public class PropertyDetailDTO {

    private Long id;
    private String title;
    private String city;
    private Double area;
    private Double price;
    private String description;   // 👈 opzionale, se hai campo in entity
    private String publishedAt;   // 👈 opzionale, se hai LocalDateTime in entity

    public PropertyDetailDTO(Long id, String title, String city, Double area, Double price, String description, String publishedAt) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
        this.description = description;
        this.publishedAt = publishedAt;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
}
