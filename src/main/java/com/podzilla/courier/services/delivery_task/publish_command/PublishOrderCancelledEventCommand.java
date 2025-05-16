package com.podzilla.courier.services.delivery_task.publish_command;

import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.OrderCancelledEvent;

public class PublishOrderCancelledEventCommand implements Command {
    private final EventPublisher eventPublisher;
    private final OrderCancelledEvent event;

    public PublishOrderCancelledEventCommand(final EventPublisher eventPublisher,
                                             final String orderId,
                                             final String courierId,
                                             final String cancellationReason) {
        this.eventPublisher = eventPublisher;
        this.event = new OrderCancelledEvent(orderId, courierId, cancellationReason);
    }

    @Override
    public void execute() {
        eventPublisher.publishEvent(EventsConstants.ORDER_CANCELLED, event);
    }
}
