package com.podzilla.courier.services;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.repositories.DeliveryTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryTaskService {

    private final DeliveryTaskRepository deliveryTaskRepository;

    public DeliveryTaskService(DeliveryTaskRepository deliveryTaskRepository) {
        this.deliveryTaskRepository = deliveryTaskRepository;
    }

    public List<DeliveryTask> getAllDeliveryTasks() {
        return deliveryTaskRepository.findAll();
    }

    public Optional<DeliveryTask> getDeliveryTaskById(String id) {
        return deliveryTaskRepository.findById(id);
    }

    public List<DeliveryTask> getDeliveryTasksByCourierId(String courierId) {
        return deliveryTaskRepository.findByCourierId(courierId);
    }

    public List<DeliveryTask> getDeliveryTasksByStatus(DeliveryStatus status) {
        return deliveryTaskRepository.findByStatus(status);
    }

    public List<DeliveryTask> getDeliveryTasksByOrderId(String orderId) {
        return deliveryTaskRepository.findByOrderId(orderId);
    }

}