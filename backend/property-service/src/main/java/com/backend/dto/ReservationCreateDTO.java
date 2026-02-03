package com.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreateDTO {
    @JsonProperty("id_prop")
    private Long idProp;
    private Long idUser;
    private String date;
    private String time;

}
