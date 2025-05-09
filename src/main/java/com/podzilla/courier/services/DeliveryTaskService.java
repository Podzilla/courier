package com.podzilla.courier.services;

import com.podzilla.courier.config.RabbitMQConfig;
import com.podzilla.courier.dtos.delivery_tasks.CancelDeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.DeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.SubmitCourierRatingResponseDto;
import com.podzilla.courier.dtos.events.OrderDeliveredEvent;
import com.podzilla.courier.dtos.events.OrderFailedEvent;
import com.podzilla.courier.dtos.events.OrderShippedEvent;
import com.podzilla.courier.events.EventPublisher;
import com.podzilla.courier.mappers.DeliveryTaskMapper;
import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.repositories.delivery_task.IDeliveryTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryTaskService {

    private final IDeliveryTaskRepository deliveryTaskRepository;
    private final EventPublisher eventPublisher;
    private static final Logger logger = LoggerFactory.getLogger(DeliveryTaskService.class);


    public DeliveryTaskService(IDeliveryTaskRepository deliveryTaskRepository, EventPublisher eventPublisher) {
        this.deliveryTaskRepository = deliveryTaskRepository;
        this.eventPublisher = eventPublisher;
    }

    public DeliveryTaskResponseDto createDeliveryTask(CreateDeliveryTaskRequestDto deliveryTaskRequestDto) {
        logger.info("Creating delivery task for order ID: {}", deliveryTaskRequestDto.getOrderId());
        DeliveryTask deliveryTask = DeliveryTaskMapper.toEntity(deliveryTaskRequestDto);
        DeliveryTask savedTask = deliveryTaskRepository.save(deliveryTask);
        logger.debug("Delivery task created with ID: {}", savedTask.getId());
        return DeliveryTaskMapper.toCreateResponseDto(savedTask);
    }

    public List<DeliveryTaskResponseDto> getAllDeliveryTasks() {
        logger.info("Fetching all delivery tasks");
        List<DeliveryTaskResponseDto> deliveryTasks = deliveryTaskRepository.findAll()
                .stream()
                .map(DeliveryTaskMapper::toCreateResponseDto)
                .collect(Collectors.toList());
        logger.debug("Delivery tasks fetched: {}", deliveryTasks);
        return deliveryTasks;
    }

    public Optional<DeliveryTaskResponseDto> getDeliveryTaskById(String id) {
        logger.info("Fetching delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            logger.debug("Delivery task found with ID: {}", deliveryTask.get().getId());
        } else {
            logger.debug("Delivery task not found with ID: {}", id);
        }
        return deliveryTask.map(DeliveryTaskMapper::toCreateResponseDto);
    }

    public List<DeliveryTaskResponseDto> getDeliveryTasksByCourierId(String courierId) {
        logger.info("Fetching delivery tasks by courier ID: {}", courierId);
        List<DeliveryTaskResponseDto> deliveryTasks = deliveryTaskRepository.findByCourierId(courierId)
                .stream()
                .map(DeliveryTaskMapper::toCreateResponseDto)
                .collect(Collectors.toList());
        logger.debug("Retrieved {} delivery tasks for courier ID: {}", deliveryTasks.size(), courierId);
        return deliveryTasks;
    }

    public List<DeliveryTaskResponseDto> getDeliveryTasksByStatus(DeliveryStatus status) {
        logger.info("Fetching delivery tasks by status: {}", status);
        List<DeliveryTaskResponseDto> deliveryTasks = deliveryTaskRepository.findByStatus(status)
                .stream()
                .map(DeliveryTaskMapper::toCreateResponseDto)
                .collect(Collectors.toList());
        logger.debug("Retrieved {} delivery tasks for status: {}", deliveryTasks.size(), status);
        return deliveryTasks;
    }

    public List<DeliveryTaskResponseDto> getDeliveryTasksByOrderId(String orderId) {
        logger.info("Fetching delivery tasks by order ID: {}", orderId);
        List<DeliveryTaskResponseDto> deliveryTasks = deliveryTaskRepository.findByOrderId(orderId)
                .stream()
                .map(DeliveryTaskMapper::toCreateResponseDto)
                .collect(Collectors.toList());
        logger.debug("Retrieved {} delivery tasks for order ID: {}", deliveryTasks.size(), orderId);
        return deliveryTasks;
    }

    public Optional<DeliveryTaskResponseDto> updateDeliveryTaskStatus(String id, DeliveryStatus status) {
        logger.info("Updating delivery task with ID: {} to {}", id, status);
        Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findById(id);
        if (updatedDeliveryTask.isPresent()) {
            DeliveryTask task = updatedDeliveryTask.get();
            task.setStatus(status);
            deliveryTaskRepository.save(task);
            logger.debug("Delivery task ID: {} updated to status: {}", id, status);
            // publish order.shipped event if status is OUT_FOR_DELIVERY
            if (status == DeliveryStatus.OUT_FOR_DELIVERY) {
                OrderShippedEvent event = new OrderShippedEvent(task.getOrderId(), task.getCourierId(), Instant.now());
                eventPublisher.publishOrderShipped(event);
            }
            return Optional.of(DeliveryTaskMapper.toCreateResponseDto(updatedDeliveryTask.get()));
        }
        logger.warn("Delivery task not found with ID: {} for status update", id);
        return Optional.empty();
    }

    public Pair<Double, Double> getDeliveryTaskLocation(String id) {
        logger.info("Fetching location for delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            Double latitude = deliveryTask.get().getCourierLatitude();
            Double longitude = deliveryTask.get().getCourierLongitude();
            logger.debug("Location for delivery task ID: {} is ({}, {})", id, latitude, longitude);
            return Pair.of(latitude, longitude);
        }
        logger.warn("Delivery task not found with ID for location: {}", id);
        return Pair.of(0.0, 0.0);
    }

    public DeliveryTaskResponseDto updateDeliveryTaskLocation(String id, Double latitude, Double longitude) {
        logger.info("Updating location for delivery task with ID: {} to ({}, {})", id, latitude, longitude);
        Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findById(id);
        if (updatedDeliveryTask.isPresent()) {
            DeliveryTask deliveryTask = updatedDeliveryTask.get();
            deliveryTask.setCourierLongitude(latitude);
            deliveryTask.setCourierLongitude(longitude);
            deliveryTaskRepository.save(deliveryTask);
            logger.debug("Location updated for delivery task ID: {}", id);
            return DeliveryTaskMapper.toCreateResponseDto(deliveryTask);
        }
        logger.warn("Delivery task not found with ID: {} for location update", id);
        return null;
    }

    public CancelDeliveryTaskResponseDto cancelDeliveryTask(String id, String cancellationReason) {
        logger.info("Cancelling delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            DeliveryTask deliveryTaskToCancel = deliveryTask.get();
            deliveryTaskToCancel.setStatus(DeliveryStatus.CANCELLED);
            deliveryTaskToCancel.setCancellationReason(cancellationReason);
            deliveryTaskRepository.save(deliveryTaskToCancel);
            logger.debug("Delivery task cancelled for delivery task ID: {}", id);
            // publish order.failed event
            OrderFailedEvent event = new OrderFailedEvent(
                    deliveryTaskToCancel.getOrderId(),
                    deliveryTaskToCancel.getCourierId(),
                    cancellationReason,
                    Instant.now()
            );
            eventPublisher.publishOrderFailed(event);
            return DeliveryTaskMapper.toCancelResponseDto(deliveryTaskToCancel);
        }
        logger.warn("Delivery task not found with ID: {}", id);
        return null;
    }

    public DeliveryTaskResponseDto deleteDeliveryTask(String id) {
        logger.info("Deleting delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            deliveryTaskRepository.delete(deliveryTask.get());
            logger.debug("Delivery task with ID: {} deleted", id);
            return DeliveryTaskMapper.toCreateResponseDto(deliveryTask.get());
        }
        logger.warn("Delivery task not found with ID: {} for deletion", id);
        return null;
    }

    public Optional<String> updateOtp(String id, String otp) {
        logger.info("Updating otp for delivery task with ID: {}", id);
        DeliveryTask task = deliveryTaskRepository.findById(id).orElse(null);
        if (task == null)
            return Optional.empty();
        task.setOtp(otp);
        deliveryTaskRepository.save(task);
        logger.debug("OTP updated for delivery task ID: {}", id);
        return Optional.of("Updated OTP");
    }

    public Optional<String> confirmOTP(String id, String otp) {
        logger.info("Confirming otp for delivery task with ID: {}", id);
        DeliveryTask task = deliveryTaskRepository.findById(id).orElse(null);
        if (task == null)
            return Optional.empty();
        String message = "Wrong OTP";
        if (task.getOtp().equals(otp)) {
            task.setStatus(DeliveryStatus.DELIVERED);
            message = "OTP confirmed";
            logger.debug("OTP confirmed for delivery task ID: {}", id);
            // publish order.delivered event
            OrderDeliveredEvent event = new OrderDeliveredEvent(
                    task.getOrderId(),
                    task.getCourierId(),
                    Instant.now(),
                    task.getCourierRating() != null ? BigDecimal.valueOf(task.getCourierRating()) : null
            );
            eventPublisher.publishOrderDelivered(event);
        } else {
            logger.debug("OTP not confirmed for delivery task ID: {}", id);
        }
        deliveryTaskRepository.save(task);
        return Optional.of(message);
    }

    public SubmitCourierRatingResponseDto submitCourierRating(String id, Double rating) {
        logger.info("Submitting courier rating for delivery task with ID: {}", id);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
        if (deliveryTask.isPresent()) {
            DeliveryTask deliveryTaskToSubmit = deliveryTask.get();
            if(!deliveryTaskToSubmit.getStatus().equals(DeliveryStatus.DELIVERED)) {
                logger.error("Delivery task status is not DELIVERED");
                throw new IllegalStateException("Task must be delivered to submit a rating");
            }
            deliveryTaskToSubmit.setCourierRating(rating);
            deliveryTaskToSubmit.setRatingTimestamp(LocalDateTime.now());
            deliveryTaskToSubmit.setUpdatedAt(LocalDateTime.now());
            deliveryTaskRepository.save(deliveryTaskToSubmit);
            return DeliveryTaskMapper.toSubmitCourierRatingResponseDto(deliveryTaskToSubmit);
        }
        logger.warn("Delivery task not found with ID: {} for courier rating", id);
        return null;
    }
}