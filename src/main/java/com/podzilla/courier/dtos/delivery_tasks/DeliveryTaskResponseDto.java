package com.podzilla.courier.dtos.delivery_tasks;


import com.podzilla.courier.models.DeliveryStatus;

public record DeliveryTaskResponseDto(String id, String orderId, String courierId, Double price, DeliveryStatus status,
                                      Double orderLatitude, Double orderLongitude, Double courierLatitude,
                                      Double courierLongitude) {
}