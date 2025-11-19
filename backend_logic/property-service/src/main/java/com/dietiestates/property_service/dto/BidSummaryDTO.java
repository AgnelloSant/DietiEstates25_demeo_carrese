package com.dietiestates.property_service.dto;

import java.time.LocalDateTime;

import lombok.*;




@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BidSummaryDTO {

    private Long propertyId;
    private String propertyTitle;
    private long count;
    private Double avgPrice;
    private LocalDateTime lastDate;

}
