package com.podzilla.courier.repositories.delivery_task;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;

import java.util.List;
import java.util.Optional;

public interface IDeliveryTaskRepository {
    Optional<DeliveryTask> findById(String id);
    List<DeliveryTask> findByCourierId(String courierId);
    List<DeliveryTask> findByStatus(DeliveryStatus status);
    List<DeliveryTask> findByOrderId(String orderId);
    DeliveryTask save(DeliveryTask deliveryTask);
    void delete(DeliveryTask deliveryTask);
    List<DeliveryTask> findAll();
}