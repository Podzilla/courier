package com.podzilla.courier.dtos.delivery_tasks;


import com.podzilla.courier.models.DeliveryStatus;

import java.math.BigDecimal;

public record DeliveryTaskResponseDto(String id, String orderId, String courierId, BigDecimal price,
                                      DeliveryStatus status, Double orderLatitude, Double orderLongitude,
                                      Double courierLatitude, Double courierLongitude) {
}
