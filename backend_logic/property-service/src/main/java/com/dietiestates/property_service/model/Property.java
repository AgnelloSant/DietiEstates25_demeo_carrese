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
    private Double price;

    @Column(nullable = false, name = "area_mq")  // "mq" = metri quadrati
    private Double area; 

    @Column(name = "published_at")
    private LocalDate publishedAt;


//coordinate per geoapify
private Double latitude;
private Double longitude;

    private boolean nearSchool;
    private boolean nearPark;
    private boolean nearTransport;

    //attributi per ricerca avanzata
      private String listingType;   // "vendita" | "affitto"
    private Integer rooms;        // numero stanze
    private String energyClass;   // es: A, B, C, ...

    @Column(name = "address")
private String address;

   @Column(name = "user_id", nullable = false)
    private Long idUser;

    private Long views; 
     

    //  Costruttore vuoto richiesto da JPA
    public Property() {}

    // Costruttore utile per creare oggetti 






    //  Getter e Setter

    public LocalDate getPublishedAt() {
    return publishedAt;
}

  public Property(Long id, String title, String description, String city, Double price, Double area,
            LocalDate publishedAt,  Double latitude, Double longitude, boolean nearSchool,
            boolean nearPark, boolean nearTransport, String listingType, Integer rooms, String energyClass, String address,Long idUser,
            Long views) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.city = city;
        this.price = price;
        this.area = area;
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
        this.idUser = idUser;
        this.views = views;
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

  public void setPublishedAt(LocalDate publishedAt) {
    this.publishedAt = publishedAt;
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

    public Long getId() {
        return id;
    }

  
    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUser() {
        return idUser;
    }
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
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

    public LocalDate getpublishedAt() {
        return publishedAt;
    }

    public void setpublishedAt(LocalDate publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Long getViews() {
        return views;
    }
    
    public void setViews(Long views) {
        this.views = views;
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
