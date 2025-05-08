package com.podzilla.courier.dtos.events;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderDeliveredEvent(String orderId, String courierId, Instant timestamp, BigDecimal courierRating) {
}
