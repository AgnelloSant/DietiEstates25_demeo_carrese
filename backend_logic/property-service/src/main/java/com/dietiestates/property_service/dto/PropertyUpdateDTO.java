package com.dietiestates.property_service.dto;

public class PropertyUpdateDTO {
    private String title;
    private String city;
    private Double area;
    private Double price;
// Tipo di inserzione: "vendita" o "affitto"
private String listingType;

// Numero stanze
private Integer rooms;

// Classe energetica: es. "A", "B", "C", ...
private String energyClass;

private String address;

    public PropertyUpdateDTO() {}

 

    public PropertyUpdateDTO(String title, String city, Double area, Double price, String listingType, Integer rooms,
            String energyClass, String address) {
        this.title = title;
        this.city = city;
        this.area = area;
        this.price = price;
        this.listingType = listingType;
        this.rooms = rooms;
        this.energyClass = energyClass;
        this.address = address;
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
