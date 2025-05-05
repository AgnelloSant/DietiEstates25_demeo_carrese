package com.dietiestates.property_service.dto;

public class PropertyUpdateDTO {
    private String title;
    private String city;
    private Double area;
    private Double price;

    public PropertyUpdateDTO() {}

    public PropertyUpdateDTO(String title, String city, Double area, Double price) {
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
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
}
