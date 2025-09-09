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
    private Double latitude;   
    private Double longitude;  
    private boolean nearSchool;    
    private boolean nearPark;      
    private boolean nearTransport;



    public PropertyDetailDTO(Long id, String title, String city, Double area, Double price, String description,
            String publishedAt, Double latitude, Double longitude, boolean nearSchool, boolean nearPark,
            boolean nearTransport) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
        this.description = description;
        this.publishedAt = publishedAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nearSchool = nearSchool;
        this.nearPark = nearPark;
        this.nearTransport = nearTransport;
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
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public boolean isNearSchool() {
        return nearSchool;
    }
    public void setNearSchool(boolean nearSchool) {
        this.nearSchool = nearSchool;
    }
    public boolean isNearPark() {
        return nearPark;
    }
    public void setNearPark(boolean nearPark) {
        this.nearPark = nearPark;
    }
    public boolean isNearTransport() {
        return nearTransport;
    }
    public void setNearTransport(boolean nearTransport) {
        this.nearTransport = nearTransport;
    }


    
}
