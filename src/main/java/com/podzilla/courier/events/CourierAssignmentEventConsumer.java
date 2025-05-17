package com.podzilla.courier.events;

import com.podzilla.courier.services.delivery_task.DeliveryTaskService;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.BaseEvent;
import com.podzilla.mq.events.OrderAssignedToCourierEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CourierAssignmentEventConsumer {
    final DeliveryTaskService deliveryTaskService;

    public CourierAssignmentEventConsumer(DeliveryTaskService deliveryTaskService) {
        this.deliveryTaskService = deliveryTaskService;
    }

    @RabbitListener(queues = EventsConstants.COURIER_ORDER_EVENT_QUEUE)
    public void handleEvent(BaseEvent event) {
        if(event instanceof OrderAssignedToCourierEvent) {
            OrderAssignedToCourierEvent courierEvent = (OrderAssignedToCourierEvent) event;
//            deliveryTaskService.createDeliveryTask(courierEvent.getOrderId(), courierEvent.getCourierId(),
//                    courierEvent.getPrice(), courierEvent.getOrderLatitude(), courierEvent.getOrderLongitude());
        }
    }
}
