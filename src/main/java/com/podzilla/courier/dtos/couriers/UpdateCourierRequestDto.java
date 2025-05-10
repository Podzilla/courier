package com.podzilla.courier.dtos.couriers;

import com.podzilla.courier.models.CourierStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateCourierRequestDto {
    @NotNull(message = "ID is required")
    private String id;
    private String name;
    private String mobileNo;
    private CourierStatus status;
}
