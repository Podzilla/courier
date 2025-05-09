package com.podzilla.courier.repositories.delivery_task;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DeliveryTaskRepository extends MongoRepository<DeliveryTask, String>, IDeliveryTaskRepository {
    @Override
    List<DeliveryTask> findByStatus(DeliveryStatus status);

    @Override
    List<DeliveryTask> findByOrderId(String orderId);

    @Override
    List<DeliveryTask> findByCourierId(String courierId);
}