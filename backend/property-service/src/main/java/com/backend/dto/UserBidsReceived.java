package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBidsReceived {

    Long bidId;
    String bidderName;
    String bidderPhone;
    Double bidAmount;
    LocalDateTime bidDate;
    String propertyName;
    String propertyAddress;
    String propertyCity;
    String propertyType;
    Double propertyPrice;

}
