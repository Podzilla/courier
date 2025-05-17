package com.podzilla.courier.dtos.delivery_tasks;

import java.math.BigDecimal;

public record SubmitCourierRatingResponseDto(String id, String orderId, String courierId, BigDecimal courierRating) {
}
