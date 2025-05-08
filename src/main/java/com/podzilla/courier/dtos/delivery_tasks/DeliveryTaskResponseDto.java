package com.podzilla.courier.dtos.delivery_tasks;

import com.podzilla.courier.models.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DeliveryTaskResponseDto {
    private String id;
    private String orderId;
    private String courierId;
    private Double price;
    private DeliveryStatus status;
    private Double orderLatitude;
    private Double orderLongitude;
    private Double courierLatitude;
    private Double courierLongitude;
}