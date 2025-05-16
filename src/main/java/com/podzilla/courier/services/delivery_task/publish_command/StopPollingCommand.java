package com.podzilla.courier.services.delivery_task.publish_command;

import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.OrderCancelledEvent;
import com.podzilla.mq.events.OrderDeliveredEvent;

public class StopPollingCommand implements Command {
    private final EventPublisher eventPublisher;
    private final Object event;

    public StopPollingCommand(final EventPublisher eventPublisher,
                              Object event) {
        this.eventPublisher = eventPublisher;
        this.event = event;
    }


    @Override
    public void execute() {
        if (event instanceof OrderCancelledEvent cancelledEvent) {
            // publish order_cancelled event so that the order service stop tracking courier location
            eventPublisher.publishEvent(EventsConstants.ORDER_CANCELLED, cancelledEvent);
        } else if (event instanceof OrderDeliveredEvent deliveredEvent) {
            // publish order_delivered event so that the order service stop tracking courier location
            eventPublisher.publishEvent(EventsConstants.ORDER_DELIVERED, deliveredEvent);
        }
    }
}
