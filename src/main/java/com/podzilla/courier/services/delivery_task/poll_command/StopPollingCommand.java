package com.podzilla.courier.services.delivery_task.poll_command;

import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.OrderDeliveredEvent;
import com.podzilla.mq.events.OrderDeliveryFailedEvent;

public class StopPollingCommand implements Command {
    private final EventPublisher eventPublisher;
    private final Object event;

    public StopPollingCommand(final EventPublisher eventPublisher, final Object event) {
        this.eventPublisher = eventPublisher;
        this.event = event;
    }


    @Override
    public void execute() {
        if (event instanceof OrderDeliveryFailedEvent) {
            OrderDeliveryFailedEvent cancelledEvent = (OrderDeliveryFailedEvent) event;
            // publish order_cancelled event so that the order service stops tracking courier location
            eventPublisher.publishEvent(EventsConstants.ORDER_DELIVERY_FAILED, cancelledEvent);
        } else if (event instanceof OrderDeliveredEvent) {
            OrderDeliveredEvent deliveredEvent = (OrderDeliveredEvent) event;
            // publish order_delivered event so that the order service stops tracking courier location
            eventPublisher.publishEvent(EventsConstants.ORDER_DELIVERED, deliveredEvent);
        }
    }
}
