package com.podzilla.courier.dtos.delivery_tasks;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfirmDeliveryDto {
    @NotNull(message = "Confirmation input in required")
    private String confirmationInput;
}
