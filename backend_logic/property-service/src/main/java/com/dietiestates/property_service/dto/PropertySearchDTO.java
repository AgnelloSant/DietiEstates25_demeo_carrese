package com.dietiestates.property_service.dto;

public class PropertySearchDTO {

    private String title;
    private String city;
    private Double area;
    private Double price;
private long id;

    // Costruttore
    public PropertySearchDTO(long id,String title, String city, Double area, Double price) {
        this.id= id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
    }
    // Getters e Setters
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
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "PropertySearchDTO [title=" + title + ", city=" + city + ", area=" + area + ", price=" + price + ", id="
                + id + "]";
    }
    
}