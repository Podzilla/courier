package com.podzilla.courier.services.delivery_task.confirmation_strategy;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.services.delivery_task.publish_command.StopPollingCommand;
import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.events.OrderDeliveredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class SignatureConfirmationStrategy implements DeliveryConfirmationStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureConfirmationStrategy.class);
    private final EventPublisher eventPublisher;

    public SignatureConfirmationStrategy(final EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<String> confirmDelivery(final DeliveryTask task, final String confirmationInput) {
        LOGGER.info("Confirming delivery with signature for task ID: {}", task.getId());
        // assume signature is valid if input is non-empty
        if (confirmationInput == null || confirmationInput.isEmpty()
                || !task.getSignature().equals(confirmationInput)) {
            LOGGER.debug("Invalid signature for task ID: {}", task.getId());
            return Optional.of("Invalid signature");
        }

        task.setStatus(DeliveryStatus.DELIVERED);
        LOGGER.debug("Signature confirmed for task ID: {}", task.getId());

        // publish order.delivered event
        OrderDeliveredEvent event = new OrderDeliveredEvent(
                task.getOrderId(),
                task.getCourierId(),
                task.getCourierRating() != null ? BigDecimal.valueOf(task.getCourierRating()) : null
        );
        StopPollingCommand stopPollingCommand = new StopPollingCommand(eventPublisher, event);
        stopPollingCommand.execute();

        return Optional.of("Signature confirmed");
    }
}
