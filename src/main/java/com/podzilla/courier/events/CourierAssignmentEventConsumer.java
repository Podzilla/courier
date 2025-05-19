package com.podzilla.courier.events;

import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
import com.podzilla.courier.services.delivery_task.DeliveryTaskService;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.BaseEvent;
import com.podzilla.mq.events.OrderAssignedToCourierEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CourierAssignmentEventConsumer {
    private final DeliveryTaskService deliveryTaskService;

    public CourierAssignmentEventConsumer(final DeliveryTaskService deliveryTaskService) {
        this.deliveryTaskService = deliveryTaskService;
    }

    @RabbitListener(queues = EventsConstants.COURIER_ORDER_EVENT_QUEUE)
    public void handleEvent(final BaseEvent event) {
        if (event instanceof OrderAssignedToCourierEvent) {
            OrderAssignedToCourierEvent courierEvent = (OrderAssignedToCourierEvent) event;
            CreateDeliveryTaskRequestDto deliveryTask = new CreateDeliveryTaskRequestDto(
                    courierEvent.getOrderId(),
                    courierEvent.getCourierId(),
                    courierEvent.getTotalAmount(),
                    courierEvent.getOrderLatitude(),
                    courierEvent.getOrderLongitude(),
                    courierEvent.getConfirmationType(),
                    courierEvent.getSignature());
            deliveryTaskService.createDeliveryTask(deliveryTask);
        }
    }

    @RabbitListener(queues = EventsConstants.COURIER_USER_EVENT_QUEUE)
    public void handleUserEvent(final BaseEvent event) {
        System.out.println("Received user event: " + event);
    }
}
