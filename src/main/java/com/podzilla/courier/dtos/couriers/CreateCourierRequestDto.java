package com.podzilla.courier.dtos.couriers;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class CreateCourierRequestDto {
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Mobile number is required")
    private String mobileNo;
}
