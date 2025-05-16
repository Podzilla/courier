package com.podzilla.courier.services.delivery_task.publish_command;

import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.OrderOutForDeliveryEvent;

public class StartPollingCommand implements Command {

    private final EventPublisher eventPublisher;
    private final OrderOutForDeliveryEvent event;

    public StartPollingCommand(final EventPublisher eventPublisher, OrderOutForDeliveryEvent event) {
        this.eventPublisher = eventPublisher;
        this.event = event;
    }

    @Override
    public void execute() {
        // publish out_for_delivery event so that the order service start tracking courier location
        eventPublisher.publishEvent(EventsConstants.ORDER_OUT_FOR_DELIVERY, event);
    }
}
