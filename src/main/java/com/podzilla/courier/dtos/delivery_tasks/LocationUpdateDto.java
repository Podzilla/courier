package com.podzilla.courier.dtos.delivery_tasks;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocationUpdateDto {
    private Double latitude;
    private Double longitude;
}