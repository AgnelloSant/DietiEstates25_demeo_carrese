package com.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidCreateDTO {
    @JsonProperty("id_prop")
    private Long propertyId;
    private Double amount;

}
