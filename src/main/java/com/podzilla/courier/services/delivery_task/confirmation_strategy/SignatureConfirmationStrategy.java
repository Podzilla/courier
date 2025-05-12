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
public class SignatureConfirmationStrategy implements DeliveryConfirmationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(SignatureConfirmationStrategy.class);
    private final EventPublisher eventPublisher;

    public SignatureConfirmationStrategy(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<String> confirmDelivery(DeliveryTask task, String confirmationInput) {
        logger.info("Confirming delivery with signature for task ID: {}", task.getId());
        // assume signature is valid if input is non-empty
        if (confirmationInput == null || confirmationInput.isEmpty() || !task.getSignature().equals(confirmationInput)) {
            logger.debug("Invalid signature for task ID: {}", task.getId());
            return Optional.of("Invalid signature");
        }

        task.setStatus(DeliveryStatus.DELIVERED);
        logger.debug("Signature confirmed for task ID: {}", task.getId());

        // publish order.delivered event
        OrderDeliveredEvent event = new OrderDeliveredEvent(
                task.getOrderId(),
                task.getCourierId(),
                Instant.now(),
                task.getCourierRating() != null ? BigDecimal.valueOf(task.getCourierRating()) : null
        );
        eventPublisher.publishOrderDelivered(event);

        return Optional.of("Signature confirmed");
    }
}