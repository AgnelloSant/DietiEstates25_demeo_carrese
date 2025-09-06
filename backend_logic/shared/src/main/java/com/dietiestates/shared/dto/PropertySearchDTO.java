package com.dietiestates.shared.dto;

public class PropertySearchDTO {
  private Long id;
  private String title;
  private String city;
  private Double area;
  private Double price;

  public PropertySearchDTO() {}

  public PropertySearchDTO(Long id, String title, String city, Double area, Double price) {
    this.id = id;
    this.title = title;
    this.city = city;
    this.area = area;
    this.price = price;
  }

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
