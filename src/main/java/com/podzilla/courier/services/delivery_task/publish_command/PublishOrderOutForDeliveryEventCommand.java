package com.podzilla.courier.services.delivery_task.publish_command;

import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.OrderOutForDeliveryEvent;

public class PublishOrderOutForDeliveryEventCommand implements Command {

    private final EventPublisher eventPublisher;
    private final OrderOutForDeliveryEvent event;

    public PublishOrderOutForDeliveryEventCommand(final EventPublisher eventPublisher,
                                                  final String orderId,
                                                  final String courierId) {
        this.eventPublisher = eventPublisher;
        this.event = new OrderOutForDeliveryEvent(orderId, courierId);
    }

    @Override
    public void execute() {
        eventPublisher.publishEvent(EventsConstants.ORDER_OUT_FOR_DELIVERY, event);
    }
}
