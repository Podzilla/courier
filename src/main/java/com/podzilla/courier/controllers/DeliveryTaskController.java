package com.podzilla.courier.controllers;

import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.DeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.LocationUpdateDto;
import com.podzilla.courier.dtos.delivery_tasks.UpdateDeliveryStatusRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.services.DeliveryTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/delivery-tasks")
public class DeliveryTaskController {

    private final DeliveryTaskService deliveryTaskService;
    private static final Logger logger = LoggerFactory.getLogger(DeliveryTaskController.class);

    public DeliveryTaskController(DeliveryTaskService deliveryTaskService) {
        this.deliveryTaskService = deliveryTaskService;
    }

    @PostMapping
    public ResponseEntity<DeliveryTaskResponseDto> createDeliveryTask(@RequestBody CreateDeliveryTaskRequestDto deliveryTaskRequestDto) {
        logger.info("Received request to create delivery task");
        return ResponseEntity.ok(deliveryTaskService.createDeliveryTask(deliveryTaskRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Optional<DeliveryTaskResponseDto>> updateDeliveryTaskStatus(@PathVariable String id, @RequestBody UpdateDeliveryStatusRequestDto statusDto) {
        logger.info("Received request to update delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.updateDeliveryTaskStatus(id, statusDto.getStatus()));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryTaskResponseDto>> getAllDeliveryTasks() {
        logger.info("Received request to get all delivery tasks");
        return ResponseEntity.ok(deliveryTaskService.getAllDeliveryTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryTaskResponseDto> getDeliveryTaskById(@PathVariable String id) {
        logger.info("Received request to get delivery task with id {}", id);
        return deliveryTaskService.getDeliveryTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<DeliveryTaskResponseDto>> getDeliveryTasksByCourierId(@PathVariable String courierId) {
        logger.info("Received request to get delivery task with courier id {}", courierId);
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByCourierId(courierId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryTaskResponseDto>> getDeliveryTasksByStatus(@PathVariable DeliveryStatus status) {
        logger.info("Received request to get delivery task with status {}", status);
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByStatus(status));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryTaskResponseDto>> getDeliveryTasksByOrderId(@PathVariable String orderId) {
        logger.info("Received request to get delivery task with order id {}", orderId);
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByOrderId(orderId));
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<Pair<Double, Double>> getDeliveryTaskLocation(@PathVariable String id) {
        logger.info("Received request to get the location of delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTaskLocation(id));
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<DeliveryTaskResponseDto> updateDeliveryTaskLocation(@PathVariable String id, @RequestBody LocationUpdateDto locationUpdateDto) {
        logger.info("Received request to update the location if delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.updateDeliveryTaskLocation(id, locationUpdateDto.getLatitude(), locationUpdateDto.getLongitude()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeliveryTaskResponseDto> deleteDeliveryTask(@PathVariable String id) {
        logger.info("Received request to delete delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.deleteDeliveryTask(id));
    }

}