package com.podzilla.courier.dtos.delivery_tasks;

import com.podzilla.mq.events.ConfirmationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CreateDeliveryTaskRequestDto {
    @NotNull(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Courier ID is required")
    private String courierId;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @NotNull(message = "Order latitude is required")
    private double orderLatitude;

    @NotNull(message = "Order longitude is required")
    private double orderLongitude;

    @NotNull(message = "Confirmation type is required")
    private ConfirmationType confirmationType;

    private String signature;
}
