package com.podzilla.courier.dtos.delivery_tasks;

public record SubmitCourierRatingResponseDto(String id, String orderId, String courierId, Double courierRating) {
}
