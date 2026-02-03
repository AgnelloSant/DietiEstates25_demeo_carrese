package com.backend.dto;

public class BidTrendDTO {
    private String dateLabel;
    private Long offerCount;

    public BidTrendDTO(String date, Long offerCount) {
        this.offerCount = offerCount; // offerte del giorno (tutte le proprietà insieme)
        this.dateLabel = date;
    }

    public Long getofferCount() {
        return offerCount;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setofferCount(Long offerCount) {
        this.offerCount = offerCount;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

}
