package com.podzilla.courier.services.delivery_task.confirmation_strategy;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.services.delivery_task.poll_command.StopPollingCommand;
import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.events.OrderDeliveredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class QrCodeConfirmationStrategy implements DeliveryConfirmationStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeConfirmationStrategy.class);
    private final EventPublisher eventPublisher;

    public QrCodeConfirmationStrategy(final EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<String> confirmDelivery(final DeliveryTask task, final String confirmationInput) {
        LOGGER.info("Confirming delivery with QR code for task ID: {}", task.getId());
        // assume QR code is valid if it matches a predefined value or logic
        String expectedQrCode = task.getQrCode();
        if (expectedQrCode == null || !expectedQrCode.equals(confirmationInput)) {
            LOGGER.debug("Invalid QR code for task ID: {}", task.getId());
            return Optional.of("Invalid QR code");
        }

        task.setStatus(DeliveryStatus.DELIVERED);
        LOGGER.debug("QR code confirmed for task ID: {}", task.getId());

        // publish order.delivered event
        OrderDeliveredEvent event = new OrderDeliveredEvent(
                task.getOrderId(),
                task.getCourierId(),
                task.getCourierRating()
        );
        StopPollingCommand stopPollingCommand = new StopPollingCommand(eventPublisher, event);
        stopPollingCommand.execute();

        return Optional.of("QR code confirmed");
    }
}
