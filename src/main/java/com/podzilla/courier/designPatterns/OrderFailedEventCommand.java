package com.podzilla.courier.designPatterns;

import com.podzilla.courier.dtos.events.OrderFailedEvent;
import com.podzilla.courier.events.EventPublisher;

import java.time.Instant;

public class OrderFailedEventCommand implements OrderEventCommand {

    private final String orderId;
    private final String courierId;
    private final String cancellationReason;
    private final Instant failedAt;
    private final EventPublisher eventPublisher;

    public OrderFailedEventCommand(
            final String orderId,
            final String courierId,
            final String cancellationReason,
            final Instant failedAt,
            final EventPublisher eventPublisher
    ) {
        this.orderId = orderId;
        this.courierId = courierId;
        this.cancellationReason = cancellationReason;
        this.failedAt = failedAt;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute() {
        OrderFailedEvent event = new OrderFailedEvent(orderId, courierId, cancellationReason, failedAt);
        eventPublisher.publishOrderFailed(event);
    }
}
