package com.dietiestates.shared.dto;

public class PropertySearchDTO {
    private Long id;
    private String title;
    private String city;
    private Double area;
    private Double price;

    private boolean nearSchool;    // per geoapify
    private boolean nearPark;
    private boolean nearTransport;

    public PropertySearchDTO() {}

    // 👇 Costruttore "vecchio" usato in più punti (senza i campi geoapify)
    public PropertySearchDTO(Long id, String title, String city, Double area, Double price) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
    }

    // 👇 Costruttore "nuovo" con anche i campi geoapify
    public PropertySearchDTO(Long id, String title, String city, Double area, Double price,
                             boolean nearSchool, boolean nearPark, boolean nearTransport) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
        this.nearSchool = nearSchool;
        this.nearPark = nearPark;
        this.nearTransport = nearTransport;
    }

    // Getter e Setter
    public boolean isNearSchool() { return nearSchool; }
    public void setNearSchool(boolean nearSchool) { this.nearSchool = nearSchool; }

    public boolean isNearPark() { return nearPark; }
    public void setNearPark(boolean nearPark) { this.nearPark = nearPark; }

    public boolean isNearTransport() { return nearTransport; }
    public void setNearTransport(boolean nearTransport) { this.nearTransport = nearTransport; }

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
}
