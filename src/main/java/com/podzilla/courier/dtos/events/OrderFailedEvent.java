package com.podzilla.courier.dtos.events;

import java.time.Instant;

public record OrderFailedEvent(String orderId, String courierId, String reason, Instant timestamp) {
}
