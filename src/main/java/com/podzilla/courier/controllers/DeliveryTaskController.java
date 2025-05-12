package com.podzilla.courier.controllers;

import com.podzilla.courier.dtos.delivery_tasks.SubmitCourierRatingResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.SubmitCourierRatingRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.CancelDeliveryTaskRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.LocationUpdateDto;
import com.podzilla.courier.dtos.delivery_tasks.CancelDeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.DeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.UpdateDeliveryStatusRequestDto;
import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.services.delivery_task.DeliveryTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery-tasks")
public class DeliveryTaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryTaskController.class);

    private final DeliveryTaskService deliveryTaskService;

    public DeliveryTaskController(final DeliveryTaskService deliveryTaskService) {
        this.deliveryTaskService = deliveryTaskService;
    }

    @PostMapping
    @Operation(summary = "Create delivery task", description = "Creates a new delivery task")
    @ApiResponse(responseCode = "200", description = "Delivery task created successfully")
    public ResponseEntity<DeliveryTaskResponseDto> createDeliveryTask(
            @RequestBody(description = "Delivery task creation details")
            @org.springframework.web.bind.annotation.RequestBody final CreateDeliveryTaskRequestDto requestDto) {
        LOGGER.info("Received request to create delivery task");
        return ResponseEntity.ok(deliveryTaskService.createDeliveryTask(requestDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update delivery task status", description = "Updates the status of a delivery task")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "404", description = "Delivery task not found")
    public ResponseEntity<Optional<DeliveryTaskResponseDto>> updateDeliveryTaskStatus(
            @Parameter(description = "ID of the delivery task")
            @PathVariable final String id,
            @RequestBody(description = "New status details")
            @org.springframework.web.bind.annotation.RequestBody final UpdateDeliveryStatusRequestDto statusDto) {
        LOGGER.info("Received request to update delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.updateDeliveryTaskStatus(id, statusDto.getStatus()));
    }

    @GetMapping
    @Operation(summary = "Get all delivery tasks", description = "Retrieves all delivery tasks")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all tasks")
    public ResponseEntity<List<DeliveryTaskResponseDto>> getAllDeliveryTasks() {
        LOGGER.info("Received request to get all delivery tasks");
        return ResponseEntity.ok(deliveryTaskService.getAllDeliveryTasks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery task by ID", description = "Retrieves a specific delivery task")
    @ApiResponse(responseCode = "200", description = "Delivery task found")
    @ApiResponse(responseCode = "404", description = "Delivery task not found")
    public ResponseEntity<DeliveryTaskResponseDto> getDeliveryTaskById(
            @Parameter(description = "ID of the delivery task")
            @PathVariable final String id) {
        LOGGER.info("Received request to get delivery task with id {}", id);
        return deliveryTaskService.getDeliveryTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/courier/{courierId}")
    @Operation(summary = "Get tasks by courier ID", description = "Retrieves delivery tasks for a specific courier")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public ResponseEntity<List<DeliveryTaskResponseDto>> getDeliveryTasksByCourierId(
            @Parameter(description = "ID of the courier")
            @PathVariable final String courierId) {
        LOGGER.info("Received request to get delivery task with courier id {}", courierId);
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByCourierId(courierId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieves delivery tasks filtered by status")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public ResponseEntity<List<DeliveryTaskResponseDto>> getDeliveryTasksByStatus(
            @Parameter(description = "Delivery status filter")
            @PathVariable final DeliveryStatus status) {
        LOGGER.info("Received request to get delivery task with status {}", status);
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByStatus(status));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get tasks by order ID", description = "Retrieves delivery tasks for a specific order")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    public ResponseEntity<List<DeliveryTaskResponseDto>> getDeliveryTasksByOrderId(
            @Parameter(description = "ID of the order")
            @PathVariable final String orderId) {
        LOGGER.info("Received request to get delivery task with order id {}", orderId);
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTasksByOrderId(orderId));
    }

    @GetMapping("/{id}/location")
    @Operation(summary = "Get task location", description = "Retrieves coordinates of a delivery task")
    @ApiResponse(responseCode = "200", description = "Location retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<Pair<Double, Double>> getDeliveryTaskLocation(
            @Parameter(description = "ID of the delivery task")
            @PathVariable final String id) {
        LOGGER.info("Received request to get the location of delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.getDeliveryTaskLocation(id));
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Update task location", description = "Updates coordinates of a delivery task")
    @ApiResponse(responseCode = "200", description = "Location updated successfully")
    public ResponseEntity<DeliveryTaskResponseDto> updateDeliveryTaskLocation(
            @Parameter(description = "ID of the delivery task")
            @PathVariable final String id,
            @RequestBody(description = "New coordinates data")
            @org.springframework.web.bind.annotation.RequestBody final LocationUpdateDto locationUpdateDto) {
        LOGGER.info("Received request to update the location of delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.updateDeliveryTaskLocation(
                id,
                locationUpdateDto.getLatitude(),
                locationUpdateDto.getLongitude()));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel delivery task", description = "Cancels a delivery task with a reason")
    @ApiResponse(responseCode = "200", description = "Task cancelled successfully")
    public ResponseEntity<CancelDeliveryTaskResponseDto> cancelDeliveryTask(
            @Parameter(description = "ID of the delivery task")
            @PathVariable final String id,
            @RequestBody(description = "Cancellation details")
            @org.springframework.web.bind.annotation.RequestBody final CancelDeliveryTaskRequestDto cancelTaskDto) {
        LOGGER.info("Received request to cancel delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.cancelDeliveryTask(id, cancelTaskDto.cancellationReason()));
    }

    @PatchMapping("/{id}/rate")
    @Operation(summary = "Submit courier rating", description = "Submits a rating for the courier's performance")
    @ApiResponse(responseCode = "200", description = "Rating submitted successfully")
    public ResponseEntity<SubmitCourierRatingResponseDto> submitCourierRating(
            @Parameter(description = "ID of the delivery task")
            @PathVariable final String id,
            @RequestBody(description = "Rating details")
            @org.springframework.web.bind.annotation.RequestBody final SubmitCourierRatingRequestDto ratingDto) {
        return ResponseEntity.ok(deliveryTaskService.submitCourierRating(id, ratingDto.getRating()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete delivery task", description = "Permanently removes a delivery task")
    @ApiResponse(responseCode = "200", description = "Task deleted successfully")
    public ResponseEntity<DeliveryTaskResponseDto> deleteDeliveryTask(
            @Parameter(description = "ID of the delivery task")
            @PathVariable final String id) {
        LOGGER.info("Received request to delete delivery task with id {}", id);
        return ResponseEntity.ok(deliveryTaskService.deleteDeliveryTask(id));
    }

    @PutMapping("/{id}/confirmation")
    @Operation(summary = "Confirm delivery", description = "Validates the delivery confirmation input (e.g., OTP, QR code, or signature)")
    @ApiResponse(responseCode = "200", description = "Delivery confirmed successfully")
    @ApiResponse(responseCode = "404", description = "Invalid confirmation input or task not found")
    public ResponseEntity<String> confirmDelivery(
            @Parameter(description = "ID of the delivery task")
            @PathVariable final String id,
            @RequestBody(description = "Confirmation input (e.g., OTP, QR code, or signature)")
            @org.springframework.web.bind.annotation.RequestBody final String confirmationInput) {
        LOGGER.info("Received request to confirm delivery for task ID: {}", id);
        return deliveryTaskService.confirmDelivery(id, confirmationInput)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
