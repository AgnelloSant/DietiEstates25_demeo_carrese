package com.dietiestates.property_service.dto;

import java.time.LocalDate;

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
// Tipo di inserzione: "vendita" o "affitto"
private String listingType;

// Numero stanze
private Integer rooms;

// Classe energetica: es. "A", "B", "C", ...
private String energyClass;

private String address;

    // Costruttori
    public PropertyCreateDTO() {}


    public PropertyCreateDTO(String title, String city, Double area, Double price, LocalDate publishedAt,
            Double latitude, Double longitude, Boolean nearSchool, Boolean nearPark, Boolean nearTransport,
            String listingType, Integer rooms, String energyClass, String address) {
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
        this.publishedAt = publishedAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nearSchool = nearSchool;
        this.nearPark = nearPark;
        this.nearTransport = nearTransport;
        this.listingType = listingType;
        this.rooms = rooms;
        this.energyClass = energyClass;
        this.address = address;
    }

    // Getter e Setter


    public String getTitle() {
        return title;
    }




    public void setTitle(String title) {
        this.title = title;
    }




    public String getCity() {
        return city;
    }




    public void setCity(String city) {
        this.city = city;
    }




    public Double getArea() {
        return area;
    }




    public void setArea(Double area) {
        this.area = area;
    }




    public Double getPrice() {
        return price;
    }




    public void setPrice(Double price) {
        this.price = price;
    }




    public LocalDate getPublishedAt() {
        return publishedAt;
    }




    public void setPublishedAt(LocalDate publishedAt) {
        this.publishedAt = publishedAt;
    }




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




    public Boolean getNearSchool() {
        return nearSchool;
    }




    public void setNearSchool(Boolean nearSchool) {
        this.nearSchool = nearSchool;
    }




    public Boolean getNearPark() {
        return nearPark;
    }




    public void setNearPark(Boolean nearPark) {
        this.nearPark = nearPark;
    }




    public Boolean getNearTransport() {
        return nearTransport;
    }




    public void setNearTransport(Boolean nearTransport) {
        this.nearTransport = nearTransport;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getEnergyClass() {
        return energyClass;
    }

    public void setEnergyClass(String energyClass) {
        this.energyClass = energyClass;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }




    


    
}