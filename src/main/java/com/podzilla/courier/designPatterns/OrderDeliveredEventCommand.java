package com.podzilla.courier.designPatterns;

import com.podzilla.courier.dtos.events.OrderDeliveredEvent;
import com.podzilla.courier.events.EventPublisher;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderDeliveredEventCommand implements OrderEventCommand {

    private final String orderId;
    private final String courierId;
    private final Instant deliveredAt;
    private final BigDecimal courierRating;
    private final EventPublisher eventPublisher;

    public OrderDeliveredEventCommand(
            final String orderId,
            final String courierId,
            final Instant deliveredAt,
            final BigDecimal courierRating,
            final EventPublisher eventPublisher
    ) {
        this.orderId = orderId;
        this.courierId = courierId;
        this.deliveredAt = deliveredAt;
        this.courierRating = courierRating;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute() {
        OrderDeliveredEvent event = new OrderDeliveredEvent(orderId, courierId, deliveredAt, courierRating);
        eventPublisher.publishOrderDelivered(event);
    }
}
