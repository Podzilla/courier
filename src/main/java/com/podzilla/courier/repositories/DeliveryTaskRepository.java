package com.podzilla.courier.repositories;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
public interface DeliveryTaskRepository extends MongoRepository<DeliveryTask, String> {
    List<DeliveryTask> findByStatus(DeliveryStatus status);
    List<DeliveryTask> findByOrderId(String orderId);
    List<DeliveryTask> findByCourierId(String courierId);
}