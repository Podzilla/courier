package com.podzilla.courier.dtos.delivery_tasks;

public record CancelDeliveryTaskResponseDto(String id, String orderId, String courierId, String cancellationReason) {
}
