package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReservation {

    Long propertyId;
    String userName;
    String phoneNumber;
    String reservationDate;
    String reservationTime;
    String propertyName;
    String propertyAddress;
    String propertyCity;
    String propertyType;
    Double propertyPrice;

}
