package com.podzilla.courier.services.delivery_task.confirmation_strategy;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.EventsConstants;
import com.podzilla.mq.events.OrderDeliveredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class OtpConfirmationStrategy implements DeliveryConfirmationStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(OtpConfirmationStrategy.class);
    private final EventPublisher eventPublisher;

    public OtpConfirmationStrategy(final EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<String> confirmDelivery(final DeliveryTask task, final String confirmationInput) {
        LOGGER.info("Confirming delivery with OTP for task ID: {}", task.getId());
        if (task.getOtp() == null || !task.getOtp().equals(confirmationInput)) {
            LOGGER.debug("Invalid OTP for task ID: {}", task.getId());
            return Optional.of("Wrong OTP");
        }

        task.setStatus(DeliveryStatus.DELIVERED);
        LOGGER.debug("OTP confirmed for task ID: {}", task.getId());

        com.podzilla.mq.events.OrderDeliveredEvent event = new OrderDeliveredEvent(
                task.getOrderId(),
                task.getCourierId(),
                task.getCourierRating() != null ? BigDecimal.valueOf(task.getCourierRating()) : null
        );
        eventPublisher.publishEvent(EventsConstants.ORDER_DELIVERED, event);

        return Optional.of("OTP confirmed");
    }
}
