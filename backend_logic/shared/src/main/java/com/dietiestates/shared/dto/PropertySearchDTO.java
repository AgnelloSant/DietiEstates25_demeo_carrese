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

//ricerca avanzata
   private String listingType;
    private Integer rooms;
    private String energyClass;
private String address;
    private Double latitude;
    private Double longitude;

    public PropertySearchDTO() {}

    // 👇 Costruttore "vecchio" usato in più punti (senza i campi geoapify)
    public PropertySearchDTO(Long id, String title, String city, Double area, Double price) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
    }

    // 👇 Costruttore "nuovo" con anche i campi geoapify e ricerca avanzata
    
    public PropertySearchDTO(Long id, String title, String city, Double area, Double price,
        boolean nearSchool, boolean nearPark, boolean nearTransport, String listingType, Integer rooms,
        String energyClass,String address,Double latitude,Double longitude) {
    this.id = id;
    this.title = title;
    this.city = city;
    this.area = area;
    this.price = price;
    this.nearSchool = nearSchool;
    this.nearPark = nearPark;
    this.nearTransport = nearTransport;
    this.listingType = listingType;
    this.rooms = rooms;
    this.energyClass = energyClass;
     this.address = address;
     this.latitude=latitude;
     this.longitude=longitude;
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




    
}
