package com.dietiestates.property_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BidCreateDTO {
    @JsonProperty("id_prop")
    private Long propertyId;
    private Double amount;

    public BidCreateDTO() {}
    public BidCreateDTO(Long propertyId, Double amount) {
        this.propertyId = propertyId;
        this.amount = amount;
    }
    public Long getPropertyId() {
        return propertyId;
    }
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
