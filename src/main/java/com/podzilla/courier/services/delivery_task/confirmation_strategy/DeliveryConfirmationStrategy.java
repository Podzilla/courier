package com.podzilla.courier.services.delivery_task.confirmation_strategy;

import com.podzilla.courier.models.DeliveryTask;

import java.util.Optional;

public interface DeliveryConfirmationStrategy {
    Optional<String> confirmDelivery(DeliveryTask task, String confirmationInput);
}
