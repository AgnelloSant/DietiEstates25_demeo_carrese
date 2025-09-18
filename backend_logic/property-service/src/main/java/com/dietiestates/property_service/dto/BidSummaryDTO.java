package com.dietiestates.property_service.dto;

import java.time.LocalDateTime;

public class BidSummaryDTO {
    private Long propertyId;
    private String propertyTitle;
    private long count;
    private Double avgPrice;
    private LocalDateTime lastDate;

    public BidSummaryDTO(Long propertyId, String propertyTitle, long count, Double avgPrice, LocalDateTime lastDate) {
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.count = count;
        this.avgPrice = avgPrice;
        this.lastDate = lastDate;
    }
    // getters e setters
    public Long getPropertyId() {
        return propertyId;
    }
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    public String getPropertyTitle() {
        return propertyTitle;
    }
    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }
    public Double getAvgPrice() {
        return avgPrice;
    }
    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;       
    }
    public LocalDateTime getLastDate() {
        return lastDate;
    }
    public void setLastDate(LocalDateTime lastDate) {   
        this.lastDate = lastDate;
    }   
    
}
