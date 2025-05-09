package com.podzilla.courier.events;

import com.podzilla.courier.dtos.events.OrderDeliveredEvent;
import com.podzilla.courier.dtos.events.OrderFailedEvent;
import com.podzilla.courier.dtos.events.OrderShippedEvent;

public interface EventPublisher {
    void publishOrderShipped(OrderShippedEvent event);
    void publishOrderDelivered(OrderDeliveredEvent event);
    void publishOrderFailed(OrderFailedEvent event);
}