package com.podzilla.courier.events;

import com.podzilla.courier.dtos.couriers.CreateCourierRequestDto;
import com.podzilla.courier.services.courier.CourierService;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.BaseEvent;
import com.podzilla.mq.events.CourierRegisteredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CourierRegisteredEventConsumer {
    private final CourierService courierService;

    public CourierRegisteredEventConsumer(final CourierService courierService) {
        this.courierService = courierService;
    }

    @RabbitListener(queues = EventsConstants.COURIER_USER_EVENT_QUEUE)
    public void consumeCourierRegisteredEvent(final BaseEvent event) {
        if (event instanceof CourierRegisteredEvent) {
            CourierRegisteredEvent courierRegisteredEvent = (CourierRegisteredEvent) event;
            CreateCourierRequestDto courier = new CreateCourierRequestDto(
                    courierRegisteredEvent.getCourierId(),
                    courierRegisteredEvent.getName(),
                    courierRegisteredEvent.getMobileNo()
            );
            courierService.createCourier(courier);
        }
    }
}
