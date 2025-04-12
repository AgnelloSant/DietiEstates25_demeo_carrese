package com.dietiestates.property_service.dto;

public class PropertyDTO {

    private String title;
    private String city;
    private Double area;
    private Double price;

    // Costruttore
    public PropertyDTO(String title, String city, Double area, Double price) {
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
    @Override
    public String toString() {
        return "PropertyDTO{" +
                "title='" + title + '\'' +
                ", city='" + city + '\'' +
                ", area=" + area +
                ", price=" + price +
                '}';
    }
}