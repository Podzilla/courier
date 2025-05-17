package com.podzilla.courier.services.delivery_task.poll_command;

import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.repositories.delivery_task.IDeliveryTaskRepository;
import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.OrderOutForDeliveryEvent;

public class StartPollingCommand implements Command {

    private final EventPublisher eventPublisher;
    private final OrderOutForDeliveryEvent event;
    private final IDeliveryTaskRepository deliveryTaskRepository;

    public StartPollingCommand(final EventPublisher eventPublisher, final OrderOutForDeliveryEvent event,
        final IDeliveryTaskRepository deliveryTaskRepository) {
        this.eventPublisher = eventPublisher;
        this.event = event;
        this.deliveryTaskRepository = deliveryTaskRepository;
    }

    @Override
    public void execute() {
        DeliveryTask deliveryTask = deliveryTaskRepository.findByOrderId(event.getOrderId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No DeliveryTask found for orderId: " + event.getOrderId()));

        switch (deliveryTask.getConfirmationType()) {
            case OTP:
                String taskId = deliveryTask.getId();
                String otp = taskId.length() >= 4 ? taskId.substring(taskId.length() - 4) : taskId;
                deliveryTask.setOtp(otp);
                deliveryTaskRepository.save(deliveryTask);
                break;
            case QR_CODE:
                deliveryTask.setQrCode("qr-code " + deliveryTask.getId());
                deliveryTaskRepository.save(deliveryTask);
                break;
        }

        // publish out_for_delivery event so that the order service start tracking courier location
        eventPublisher.publishEvent(EventsConstants.ORDER_OUT_FOR_DELIVERY, event);
    }
}
