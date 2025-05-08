package com.podzilla.courier.dtos.events;

import java.time.Instant;

public record OrderShippedEvent(String orderId, String courierId, Instant timestamp) {
}
