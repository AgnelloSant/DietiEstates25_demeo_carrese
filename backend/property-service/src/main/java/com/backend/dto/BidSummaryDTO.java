package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidSummaryDTO {
    private Long propertyId;
    private Double avgPrice;
    private Long count;
    private String propertyTitle;
    private LocalDateTime lastDate;
}
