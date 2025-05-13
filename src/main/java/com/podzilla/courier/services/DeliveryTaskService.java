package com.podzilla.courier.services;

import com.podzilla.courier.designPatterns.OrderDeliveredEventCommand;
import com.podzilla.courier.designPatterns.OrderEventCommand;
import com.podzilla.courier.designPatterns.OrderFailedEventCommand;
import com.podzilla.courier.designPatterns.OrderShippedEventCommand;
import com.podzilla.courier.dtos.delivery_tasks.CancelDeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.DeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.SubmitCourierRatingResponseDto;
import com.podzilla.courier.events.EventPublisher;
import com.podzilla.courier.mappers.DeliveryTaskMapper;
import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.repositories.delivery_task.IDeliveryTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeliveryTaskService {

    private final IDeliveryTaskRepository deliveryTaskRepository;
    private final EventPublisher eventPublisher;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryTaskService.class);


    public DeliveryTaskService(final IDeliveryTaskRepository deliveryTaskRepository,
                               final EventPublisher eventPublisher) {
        this.deliveryTaskRepository = deliveryTaskRepository;
        this.eventPublisher = eventPublisher;
    }

    public DeliveryTaskResponseDto createDeliveryTask(final CreateDeliveryTaskRequestDto deliveryTaskRequestDto) {
        LOGGER.info("Creating delivery task for order ID: {}", deliveryTaskRequestDto.getOrderId());
        DeliveryTask deliveryTask = DeliveryTaskMapper.toEntity(deliveryTaskRequestDto);
        DeliveryTask savedTask = deliveryTaskRepository.save(deliveryTask);
        LOGGER.debug("Delivery task created with ID: {}", savedTask.getId());
        return DeliveryTaskMapper.toCreateResponseDto(savedTask);
    }

    public List<DeliveryTaskResponseDto> getAllDeliveryTasks() {
        LOGGER.info("Fetching all delivery tasks");
        List<DeliveryTaskResponseDto> deliveryTasks = deliveryTaskRepository.findAll()
                .stream()
                .map(DeliveryTaskMapper::toCreateResponseDto)
                .collect(Collectors.toList());
        LOGGER.debug("Delivery tasks fetched: {}", deliveryTasks);
        return deliveryTasks;
    }

    public Optional<DeliveryTaskResponseDto> getDeliveryTaskById(final String id) {
        LOGGER.info("Fetching delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            LOGGER.debug("Delivery task found with ID: {}", deliveryTask.get().getId());
        } else {
            LOGGER.debug("Delivery task not found with ID: {}", id);
        }
        return deliveryTask.map(DeliveryTaskMapper::toCreateResponseDto);
    }

    public List<DeliveryTaskResponseDto> getDeliveryTasksByCourierId(final String courierId) {
        LOGGER.info("Fetching delivery tasks by courier ID: {}", courierId);
        List<DeliveryTaskResponseDto> deliveryTasks = deliveryTaskRepository.findByCourierId(courierId)
                .stream()
                .map(DeliveryTaskMapper::toCreateResponseDto)
                .collect(Collectors.toList());
        LOGGER.debug("Retrieved {} delivery tasks for courier ID: {}", deliveryTasks.size(), courierId);
        return deliveryTasks;
    }

    public List<DeliveryTaskResponseDto> getDeliveryTasksByStatus(final DeliveryStatus status) {
        LOGGER.info("Fetching delivery tasks by status: {}", status);
        List<DeliveryTaskResponseDto> deliveryTasks = deliveryTaskRepository.findByStatus(status)
                .stream()
                .map(DeliveryTaskMapper::toCreateResponseDto)
                .collect(Collectors.toList());
        LOGGER.debug("Retrieved {} delivery tasks for status: {}", deliveryTasks.size(), status);
        return deliveryTasks;
    }

    public List<DeliveryTaskResponseDto> getDeliveryTasksByOrderId(final String orderId) {
        LOGGER.info("Fetching delivery tasks by order ID: {}", orderId);
        List<DeliveryTaskResponseDto> deliveryTasks = deliveryTaskRepository.findByOrderId(orderId)
                .stream()
                .map(DeliveryTaskMapper::toCreateResponseDto)
                .collect(Collectors.toList());
        LOGGER.debug("Retrieved {} delivery tasks for order ID: {}", deliveryTasks.size(), orderId);
        return deliveryTasks;
    }

    public Optional<DeliveryTaskResponseDto> updateDeliveryTaskStatus(final String id, final DeliveryStatus status) {
        LOGGER.info("Updating delivery task with ID: {} to {}", id, status);
        Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findById(id);
        if (updatedDeliveryTask.isPresent()) {
            DeliveryTask task = updatedDeliveryTask.get();
            task.setStatus(status);
            deliveryTaskRepository.save(task);
            LOGGER.debug("Delivery task ID: {} updated to status: {}", id, status);
            // publish order.shipped event if status is OUT_FOR_DELIVERY
            if (status == DeliveryStatus.OUT_FOR_DELIVERY) {
                OrderEventCommand command = new OrderShippedEventCommand(
                        task.getOrderId(), task.getCourierId(), eventPublisher
                );
                command.execute();
            }
            return Optional.of(DeliveryTaskMapper.toCreateResponseDto(updatedDeliveryTask.get()));
        }
        LOGGER.warn("Delivery task not found with ID: {} for status update", id);
        return Optional.empty();
    }

    public Pair<Double, Double> getDeliveryTaskLocation(final String id) {
        LOGGER.info("Fetching location for delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            Double latitude = deliveryTask.get().getCourierLatitude();
            Double longitude = deliveryTask.get().getCourierLongitude();
            LOGGER.debug("Location for delivery task ID: {} is ({}, {})", id, latitude, longitude);
            return Pair.of(latitude, longitude);
        }
        LOGGER.warn("Delivery task not found with ID for location: {}", id);
        return Pair.of(0.0, 0.0);
    }

    public DeliveryTaskResponseDto updateDeliveryTaskLocation(final String id, final Double latitude,
                                                              final Double longitude) {
        LOGGER.info("Updating location for delivery task with ID: {} to ({}, {})", id, latitude, longitude);
        Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findById(id);
        if (updatedDeliveryTask.isPresent()) {
            DeliveryTask deliveryTask = updatedDeliveryTask.get();
            deliveryTask.setCourierLongitude(latitude);
            deliveryTask.setCourierLongitude(longitude);
            deliveryTaskRepository.save(deliveryTask);
            LOGGER.debug("Location updated for delivery task ID: {}", id);
            return DeliveryTaskMapper.toCreateResponseDto(deliveryTask);
        }
        LOGGER.warn("Delivery task not found with ID: {} for location update", id);
        return null;
    }

    public CancelDeliveryTaskResponseDto cancelDeliveryTask(final String id, final String cancellationReason) {
        LOGGER.info("Cancelling delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            DeliveryTask deliveryTaskToCancel = deliveryTask.get();
            deliveryTaskToCancel.setStatus(DeliveryStatus.CANCELLED);
            deliveryTaskToCancel.setCancellationReason(cancellationReason);
            deliveryTaskRepository.save(deliveryTaskToCancel);
            LOGGER.debug("Delivery task cancelled for delivery task ID: {}", id);
            // publish order.failed event
            OrderEventCommand command = new OrderFailedEventCommand(
                    deliveryTaskToCancel.getOrderId(),
                    deliveryTaskToCancel.getCourierId(),
                    cancellationReason,
                    Instant.now(),
                    eventPublisher
            );
            command.execute();

            return DeliveryTaskMapper.toCancelResponseDto(deliveryTaskToCancel);
        }
        LOGGER.warn("Delivery task not found with ID: {}", id);
        return null;
    }

    public DeliveryTaskResponseDto deleteDeliveryTask(final String id) {
        LOGGER.info("Deleting delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            deliveryTaskRepository.delete(deliveryTask.get());
            LOGGER.debug("Delivery task with ID: {} deleted", id);
            return DeliveryTaskMapper.toCreateResponseDto(deliveryTask.get());
        }
        LOGGER.warn("Delivery task not found with ID: {} for deletion", id);
        return null;
    }

    public Optional<String> updateOtp(final String id, final String otp) {
        LOGGER.info("Updating otp for delivery task with ID: {}", id);
        DeliveryTask task = deliveryTaskRepository.findById(id).orElse(null);
        if (task == null) {
            return Optional.empty();
        }
        task.setOtp(otp);
        deliveryTaskRepository.save(task);
        LOGGER.debug("OTP updated for delivery task ID: {}", id);
        return Optional.of("Updated OTP");
    }

    public Optional<String> confirmOTP(final String id, final String otp) {
        LOGGER.info("Confirming otp for delivery task with ID: {}", id);
        DeliveryTask task = deliveryTaskRepository.findById(id).orElse(null);
        if (task == null) {
            return Optional.empty();
        }
        String message = "Wrong OTP";
        if (task.getOtp().equals(otp)) {
            task.setStatus(DeliveryStatus.DELIVERED);
            message = "OTP confirmed";
            LOGGER.debug("OTP confirmed for delivery task ID: {}", id);
            // publish order.delivered event
            BigDecimal rating = task.getCourierRating() != null
                    ? BigDecimal.valueOf(task.getCourierRating())
                    : null;

            OrderEventCommand command = new OrderDeliveredEventCommand(
                    task.getOrderId(),
                    task.getCourierId(),
                    Instant.now(),
                    rating,
                    eventPublisher
            );
            command.execute();
        } else {
            LOGGER.debug("OTP not confirmed for delivery task ID: {}", id);
        }
        deliveryTaskRepository.save(task);
        return Optional.of(message);
    }

    public SubmitCourierRatingResponseDto submitCourierRating(final String id, final Double rating) {
        LOGGER.info("Submitting courier rating for delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            DeliveryTask deliveryTaskToSubmit = deliveryTask.get();
            if (!deliveryTaskToSubmit.getStatus().equals(DeliveryStatus.DELIVERED)) {
                LOGGER.error("Delivery task status is not DELIVERED");
                throw new IllegalStateException("Task must be delivered to submit a rating");
            }
            deliveryTaskToSubmit.setCourierRating(rating);
            deliveryTaskToSubmit.setRatingTimestamp(LocalDateTime.now());
            deliveryTaskToSubmit.setUpdatedAt(LocalDateTime.now());
            deliveryTaskRepository.save(deliveryTaskToSubmit);
            return DeliveryTaskMapper.toSubmitCourierRatingResponseDto(deliveryTaskToSubmit);
        }
        LOGGER.warn("Delivery task not found with ID: {} for courier rating", id);
        return null;
    }
}
