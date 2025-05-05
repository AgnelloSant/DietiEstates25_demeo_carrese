package com.dietiestates.property_service.dto;

public class PropertyDeleteDTO {
    private Long id;

    public PropertyDeleteDTO() {}

    public PropertyDeleteDTO(Long id) {
        this.id = id;
    }

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}