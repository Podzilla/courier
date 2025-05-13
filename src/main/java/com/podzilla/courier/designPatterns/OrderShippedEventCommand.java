package com.podzilla.courier.designPatterns;

import com.podzilla.courier.dtos.events.OrderShippedEvent;
import com.podzilla.courier.events.EventPublisher;

import java.time.Instant;

public class OrderShippedEventCommand implements OrderEventCommand {

    private final String orderId;
    private final String courierId;
    private final EventPublisher eventPublisher;

    public OrderShippedEventCommand(final String orderId, final String courierId, final EventPublisher eventPublisher) {
        this.orderId = orderId;
        this.courierId = courierId;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute() {
        OrderShippedEvent event = new OrderShippedEvent(orderId, courierId, Instant.now());
        eventPublisher.publishOrderShipped(event);
    }
}
