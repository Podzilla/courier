package com.podzilla.courier.services;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.repositories.DeliveryTaskRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryTaskService {

    private final DeliveryTaskRepository deliveryTaskRepository;

    public DeliveryTaskService(DeliveryTaskRepository deliveryTaskRepository) {
        this.deliveryTaskRepository = deliveryTaskRepository;
    }

    public DeliveryTask createDeliveryTask(DeliveryTask deliveryTask) {
        return deliveryTaskRepository.save(deliveryTask);
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

    public Optional<DeliveryTask> updateDeliveryTaskStatus(String id, DeliveryStatus status) {
        Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findById(id);
        if (updatedDeliveryTask.isPresent()) {
            updatedDeliveryTask.get().setStatus(status);
            return Optional.of(deliveryTaskRepository.save(updatedDeliveryTask.get()));
        }
        return Optional.empty();
    }

    public Pair<Double, Double> getDeliveryTaskLocation(String id) {
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            Double latitude = deliveryTask.get().getCurrentLatitude();
            Double longitude = deliveryTask.get().getCurrentLongitude();
            return Pair.of(latitude, longitude);
        }

        return Pair.of(0.0, 0.0);
    }

    public DeliveryTask updateDeliveryTaskLocation(String id, Double latitude, Double longitude) {
        Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findById(id);
        if (updatedDeliveryTask.isPresent()) {
            DeliveryTask deliveryTask = updatedDeliveryTask.get();
            deliveryTask.setCurrentLatitude(latitude);
            deliveryTask.setCurrentLongitude(longitude);
            return deliveryTaskRepository.save(deliveryTask);
        }

        return null;
    }

    public DeliveryTask deleteDeliveryTask(String id) {
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            deliveryTaskRepository.delete(deliveryTask.get());
            return deliveryTask.get();
        }
        return null;
    }
}