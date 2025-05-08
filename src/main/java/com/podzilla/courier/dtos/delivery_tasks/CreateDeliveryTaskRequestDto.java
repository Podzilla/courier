package com.podzilla.courier.dtos.delivery_tasks;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateDeliveryTaskRequestDto {
    @NotNull(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Courier ID is required")
    private String courierId;

    @NotNull(message = "Price is required")
    private double price;

    @NotNull(message = "Order latitude is required")
    private double orderLatitude;

    @NotNull(message = "Order longitude is required")
    private double orderLongitude;
}
