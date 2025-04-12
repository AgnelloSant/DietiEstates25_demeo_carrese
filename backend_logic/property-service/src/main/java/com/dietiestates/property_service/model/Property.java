package com.dietiestates.property_service.model;


import jakarta.persistence.*;             // per JPA
import java.time.LocalDate;              // per la data di pubblicazione

@Entity
@Table(name = "properties") // nome tabella nel database
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incrementale
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String city;
    private Double area;
    private Double price;

    @Column(name = "published_at")
    private LocalDate publishedAt;

    //  Costruttore vuoto richiesto da JPA
    public Property() {}

    // Costruttore utile per creare oggetti (opzionale)
    public Property(String title, String description, String city, Double area, Double price, LocalDate publishedAt) {
        this.title = title;
        this.description = description;
        this.city = city;
        this.area = area;
        this.price = price;
        this.publishedAt = publishedAt;
    }

    //  Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
