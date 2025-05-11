package com.podzilla.courier.controllers;

import com.podzilla.courier.dtos.delivery_tasks.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    private static final Logger logger =
            LoggerFactory.getLogger(DeliveryTaskController.class);

    public DeliveryTaskController(DeliveryTaskService deliveryTaskService) {
        this.deliveryTaskService = deliveryTaskService;
    }

    @PostMapping
    @Operation(summary = "Create delivery task",
            description = "Creates a new delivery task")
    @ApiResponse(responseCode = "200",
            description = "Delivery task created successfully")
    public ResponseEntity<DeliveryTaskResponseDto> createDeliveryTask(
            @RequestBody(description = "Delivery task creation details")
            @org.springframework.web.bind.annotation.RequestBody
            CreateDeliveryTaskRequestDto deliveryTaskRequestDto) {
        logger.info("Received request to create delivery task");
        return ResponseEntity.ok(
                deliveryTaskService.createDeliveryTask(deliveryTaskRequestDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update delivery task status",
            description = "Updates the status of a delivery task")
    @ApiResponse(responseCode = "200",
            description = "Status updated successfully")
    @ApiResponse(responseCode = "404",
            description = "Delivery task not found")
    public ResponseEntity<Optional<DeliveryTaskResponseDto>>
    updateDeliveryTaskStatus(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id,
            @RequestBody(description = "New status details")
            @org.springframework.web.bind.annotation.RequestBody
            UpdateDeliveryStatusRequestDto statusDto) {
        logger.info("Received request to update delivery task with id {}", id);
        return ResponseEntity.ok(
                deliveryTaskService.updateDeliveryTaskStatus(
                        id, statusDto.getStatus()));
    }

    @GetMapping
    @Operation(summary = "Get all delivery tasks",
            description = "Retrieves all delivery tasks")
    @ApiResponse(responseCode = "200",
            description = "Successfully retrieved all tasks")
    public ResponseEntity<List<DeliveryTaskResponseDto>> getAllDeliveryTasks() {
        logger.info("Received request to get all delivery tasks");
        return ResponseEntity.ok(deliveryTaskService.getAllDeliveryTasks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery task by ID",
            description = "Retrieves a specific delivery task")
    @ApiResponse(responseCode = "200",
            description = "Delivery task found")
    @ApiResponse(responseCode = "404",
            description = "Delivery task not found")
    public ResponseEntity<DeliveryTaskResponseDto> getDeliveryTaskById(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id) {
        logger.info("Received request to get delivery task with id {}", id);
        return deliveryTaskService.getDeliveryTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/courier/{courierId}")
    @Operation(summary = "Get tasks by courier ID",
            description = "Retrieves delivery tasks for a specific courier")
    @ApiResponse(responseCode = "200",
            description = "Tasks retrieved successfully")
    public ResponseEntity<List<DeliveryTaskResponseDto>>
    getDeliveryTasksByCourierId(
            @Parameter(description = "ID of the courier")
            @PathVariable String courierId) {
        logger.info("Received request to get delivery task with courier id {}",
                courierId);
        return ResponseEntity.ok(
                deliveryTaskService.getDeliveryTasksByCourierId(courierId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status",
            description = "Retrieves delivery tasks filtered by status")
    @ApiResponse(responseCode = "200",
            description = "Tasks retrieved successfully")
    public ResponseEntity<List<DeliveryTaskResponseDto>>
    getDeliveryTasksByStatus(
            @Parameter(description = "Delivery status filter")
            @PathVariable DeliveryStatus status) {
        logger.info("Received request to get delivery task with status {}",
                status);
        return ResponseEntity.ok(
                deliveryTaskService.getDeliveryTasksByStatus(status));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get tasks by order ID",
            description = "Retrieves delivery tasks for a specific order")
    @ApiResponse(responseCode = "200",
            description = "Tasks retrieved successfully")
    public ResponseEntity<List<DeliveryTaskResponseDto>>
    getDeliveryTasksByOrderId(
            @Parameter(description = "ID of the order")
            @PathVariable String orderId) {
        logger.info("Received request to get delivery task with order id {}",
                orderId);
        return ResponseEntity.ok(
                deliveryTaskService.getDeliveryTasksByOrderId(orderId));
    }

    @GetMapping("/{id}/location")
    @Operation(summary = "Get task location",
            description = "Retrieves coordinates of a delivery task")
    @ApiResponse(responseCode = "200",
            description = "Location retrieved successfully")
    @ApiResponse(responseCode = "404",
            description = "Task not found")
    public ResponseEntity<Pair<Double, Double>> getDeliveryTaskLocation(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id) {
        logger.info("Received request to get the location of delivery task " +
                "with id {}", id);
        return ResponseEntity.ok(
                deliveryTaskService.getDeliveryTaskLocation(id));
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Update task location",
            description = "Updates coordinates of a delivery task")
    @ApiResponse(responseCode = "200",
            description = "Location updated successfully")
    public ResponseEntity<DeliveryTaskResponseDto> updateDeliveryTaskLocation(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id,
            @RequestBody(description = "New coordinates data")
            @org.springframework.web.bind.annotation.RequestBody
            LocationUpdateDto locationUpdateDto) {
        logger.info("Received request to update the location of delivery " +
                "task with id {}", id);
        return ResponseEntity.ok(
                deliveryTaskService.updateDeliveryTaskLocation(
                        id,
                        locationUpdateDto.getLatitude(),
                        locationUpdateDto.getLongitude()));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel delivery task",
            description = "Cancels a delivery task with a reason")
    @ApiResponse(responseCode = "200",
            description = "Task cancelled successfully")
    public ResponseEntity<CancelDeliveryTaskResponseDto> cancelDeliveryTask(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id,
            @RequestBody(description = "Cancellation details")
            @org.springframework.web.bind.annotation.RequestBody
            CancelDeliveryTaskRequestDto cancelTaskDto) {
        logger.info("Received request to cancel delivery task with id {}", id);
        return ResponseEntity.ok(
                deliveryTaskService.cancelDeliveryTask(
                        id, cancelTaskDto.cancellationReason()));
    }

    @PatchMapping("/{id}/rate")
    @Operation(summary = "Submit courier rating",
            description = "Submits a rating for the courier's performance")
    @ApiResponse(responseCode = "200",
            description = "Rating submitted successfully")
    public ResponseEntity<SubmitCourierRatingResponseDto> submitCourierRating(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id,
            @RequestBody(description = "Rating details")
            @org.springframework.web.bind.annotation.RequestBody
            SubmitCourierRatingRequestDto ratingDto) {
        return ResponseEntity.ok(
                deliveryTaskService.submitCourierRating(id, ratingDto.getRating()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete delivery task",
            description = "Permanently removes a delivery task")
    @ApiResponse(responseCode = "200",
            description = "Task deleted successfully")
    public ResponseEntity<DeliveryTaskResponseDto> deleteDeliveryTask(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id) {
        logger.info("Received request to delete delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.deleteDeliveryTask(id));
    }

    @PutMapping("/{id}/otp")
    @Operation(summary = "Update OTP",
            description = "Updates the One-Time Password for a delivery task")
    @ApiResponse(responseCode = "200",
            description = "OTP updated successfully")
    @ApiResponse(responseCode = "404",
            description = "Task not found")
    public ResponseEntity<String> updateOtp(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id,
            @RequestBody(description = "New OTP value")
            @org.springframework.web.bind.annotation.RequestBody String otp) {
        return deliveryTaskService.updateOtp(id, otp)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/otp-confirmation")
    @Operation(summary = "Confirm OTP",
            description = "Validates the One-Time Password for a delivery task")
    @ApiResponse(responseCode = "200",
            description = "OTP confirmed successfully")
    @ApiResponse(responseCode = "404",
            description = "Invalid OTP or task not found")
    public ResponseEntity<String> confirmOTP(
            @Parameter(description = "ID of the delivery task")
            @PathVariable String id,
            @RequestBody(description = "OTP to validate")
            @org.springframework.web.bind.annotation.RequestBody String otp) {
        return deliveryTaskService.confirmOTP(id, otp)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}