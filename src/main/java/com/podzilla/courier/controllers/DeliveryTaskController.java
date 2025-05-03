package com.podzilla.courier.controllers;

import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.services.DeliveryTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/delivery-tasks")
public class DeliveryTaskController {

    private final DeliveryTaskService deliveryTaskService;

    public DeliveryTaskController(DeliveryTaskService deliveryTaskService) {
        this.deliveryTaskService = deliveryTaskService;
    }

    @GetMapping
    public ResponseEntity<List<DeliveryTask>> getAllDeliveryTasks() {
        return ResponseEntity.ok(deliveryTaskService.getAllDeliveryTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryTask> getDeliveryTaskById(@PathVariable String id) {
        return deliveryTaskService.getDeliveryTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<DeliveryTask>> getDeliveryTasksByCourierId(@PathVariable String courierId) {
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByCourierId(courierId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryTask>> getDeliveryTasksByStatus(@PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByStatus(status));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryTask>> getDeliveryTasksByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByOrderId(orderId));
    }
}