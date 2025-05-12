package com.podzilla.courier.services.delivery_task.confirmation_strategy;

import com.podzilla.courier.dtos.events.OrderDeliveredEvent;
import com.podzilla.courier.events.EventPublisher;
import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
public class QrCodeConfirmationStrategy implements DeliveryConfirmationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(QrCodeConfirmationStrategy.class);
    private final EventPublisher eventPublisher;

    public QrCodeConfirmationStrategy(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<String> confirmDelivery(DeliveryTask task, String confirmationInput) {
        logger.info("Confirming delivery with QR code for task ID: {}", task.getId());
        // assume QR code is valid if it matches a predefined value or logic
        String expectedQrCode = task.getQrCode();
        if (expectedQrCode == null || !expectedQrCode.equals(confirmationInput)) {
            logger.debug("Invalid QR code for task ID: {}", task.getId());
            return Optional.of("Invalid QR code");
        }

        task.setStatus(DeliveryStatus.DELIVERED);
        logger.debug("QR code confirmed for task ID: {}", task.getId());

        // publish order.delivered event
        OrderDeliveredEvent event = new OrderDeliveredEvent(
                task.getOrderId(),
                task.getCourierId(),
                Instant.now(),
                task.getCourierRating() != null ? BigDecimal.valueOf(task.getCourierRating()) : null
        );
        eventPublisher.publishOrderDelivered(event);

        return Optional.of("QR code confirmed");
    }
}