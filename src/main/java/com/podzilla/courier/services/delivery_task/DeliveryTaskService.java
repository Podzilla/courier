package com.podzilla.courier.services.delivery_task;

import com.podzilla.courier.dtos.delivery_tasks.CancelDeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.DeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.SubmitCourierRatingResponseDto;
import com.podzilla.courier.mappers.DeliveryTaskMapper;
import com.podzilla.courier.models.DeliveryStatus;
import com.podzilla.courier.models.DeliveryTask;
import com.podzilla.courier.repositories.delivery_task.IDeliveryTaskRepository;
import com.podzilla.courier.services.delivery_task.confirmation_strategy.DeliveryConfirmationStrategy;
import com.podzilla.courier.services.delivery_task.confirmation_strategy.OtpConfirmationStrategy;
import com.podzilla.courier.services.delivery_task.confirmation_strategy.QrCodeConfirmationStrategy;
import com.podzilla.courier.services.delivery_task.confirmation_strategy.SignatureConfirmationStrategy;
import com.podzilla.courier.services.delivery_task.poll_command.Command;
import com.podzilla.courier.services.delivery_task.poll_command.StopPollingCommand;
import com.podzilla.courier.services.delivery_task.poll_command.StartPollingCommand;
import com.podzilla.mq.EventPublisher;
import com.podzilla.mq.events.OrderAssignedToCourierEvent.ConfirmationType;
import com.podzilla.mq.events.OrderCancelledEvent;
import com.podzilla.mq.events.OrderOutForDeliveryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DeliveryTaskService {

    private final IDeliveryTaskRepository deliveryTaskRepository;
    private final EventPublisher eventPublisher;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryTaskService.class);
    @Value("${otp.length}")
    private int otpLength;

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
                OrderOutForDeliveryEvent event = new OrderOutForDeliveryEvent(task.getOrderId(),
                        task.getCourierId());
                Command startPollingCommand = new StartPollingCommand(
                        eventPublisher,
                        event,
                        deliveryTaskRepository
                );
                startPollingCommand.execute();

                if (task.getConfirmationType() == ConfirmationType.OTP) {
                    String otp = IntStream.range(0, otpLength)
                            .mapToObj(i -> String.valueOf(task.getId().charAt(i)))
                            .collect(Collectors.joining());
                    task.setOtp(otp);
                } else if (task.getConfirmationType() == ConfirmationType.QR_CODE) {
                    String qrContent = "QR-" + task.getId();
                    task.setQrCode(qrContent);
                }
            }
            return Optional.of(DeliveryTaskMapper.toCreateResponseDto(updatedDeliveryTask.get()));
        }
        LOGGER.warn("Delivery task not found with ID: {} for status update", id);
        return Optional.empty();
    }

    public Pair<Double, Double> getDeliveryTaskLocation(final String orderId) {
        LOGGER.info("Fetching location for delivery task with order id: {}", orderId);
        Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findByOrderId(orderId).stream().findFirst();
        if (deliveryTask.isPresent()) {
            Double latitude = deliveryTask.get().getCourierLatitude();
            Double longitude = deliveryTask.get().getCourierLongitude();
            LOGGER.debug("Location for delivery task with order id: {} is ({}, {})", orderId, latitude, longitude);
            return Pair.of(latitude, longitude);
        }
        LOGGER.warn("Delivery task not found with order id: {} for location", orderId);
        return Pair.of(0.0, 0.0);
    }

    public DeliveryTaskResponseDto updateDeliveryTaskLocation(final String orderId, final Double latitude,
                                                              final Double longitude) {
        LOGGER.info("Updating location for delivery task with order id: {} to ({}, {})", orderId, latitude, longitude);
        Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findByOrderId(orderId).stream().findFirst();
        if (updatedDeliveryTask.isPresent()) {
            DeliveryTask deliveryTask = updatedDeliveryTask.get();
            deliveryTask.setCourierLongitude(latitude);
            deliveryTask.setCourierLongitude(longitude);
            deliveryTaskRepository.save(deliveryTask);
            LOGGER.debug("Location updated for delivery task with order id: {}", orderId);
            return DeliveryTaskMapper.toCreateResponseDto(deliveryTask);
        }
        LOGGER.warn("Delivery task not found with order id: {} for location update", orderId);
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
            OrderCancelledEvent event = new OrderCancelledEvent(
                    deliveryTaskToCancel.getOrderId(),
                    deliveryTaskToCancel.getCourierId(),
                    cancellationReason
            );
            Command stopPollingCommand = new StopPollingCommand(
                    eventPublisher,
                    event
            );
            stopPollingCommand.execute();

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

    public Optional<String> confirmDelivery(final String id, final String confirmationInput) {
        LOGGER.info("Confirming delivery for task ID: {}", id);
        DeliveryTask task = deliveryTaskRepository.findById(id).orElse(null);
        if (task == null) {
            LOGGER.warn("Delivery task not found with ID: {} for customer delivery confirmation", id);
            return Optional.empty();
        }

        ConfirmationType confirmationType = task.getConfirmationType();
        DeliveryConfirmationStrategy strategy = confirmationType.equals(ConfirmationType.OTP)
                ? new OtpConfirmationStrategy(eventPublisher)
                : confirmationType.equals(ConfirmationType.QR_CODE)
                ? new QrCodeConfirmationStrategy(eventPublisher) : confirmationType.equals(ConfirmationType.SIGNATURE)
                ? new SignatureConfirmationStrategy(eventPublisher) : null;
        if (strategy == null) {
            LOGGER.error("No confirmation strategy found for type: {}", confirmationType);
            return Optional.of("Invalid confirmation type");
        }

        Optional<String> result = strategy.confirmDelivery(task, confirmationInput);
        if (result.isPresent() && result.get().contains("confirmed")) {
            deliveryTaskRepository.save(task);
        }
        return result;
    }

    public SubmitCourierRatingResponseDto submitCourierRating(final String id, final BigDecimal rating) {
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
