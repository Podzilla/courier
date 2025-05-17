package com.podzilla.courier.dtos.delivery_tasks;

import com.podzilla.courier.models.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateDeliveryStatusRequestDto {
    @NotNull(message = "Status is required")
    private DeliveryStatus status;
}
