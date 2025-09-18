package com.dietiestates.property_service.dto;


public class BidCreateDTO {
    private Long propertyId;
    private Long userId;
    private Double amount;

    public BidCreateDTO() {}
    public BidCreateDTO(Long propertyId, Long userId, Double amount) {
        this.propertyId = propertyId;
        this.userId = userId;
        this.amount = amount;
    }
    public Long getPropertyId() {
        return propertyId;
    }
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
