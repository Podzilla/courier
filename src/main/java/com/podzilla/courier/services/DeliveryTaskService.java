    package com.podzilla.courier.services;

    import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
    import com.podzilla.courier.dtos.delivery_tasks.DeliveryTaskResponseDto;
    import com.podzilla.courier.mappers.DeliveryTaskMapper;
    import com.podzilla.courier.models.DeliveryStatus;
    import com.podzilla.courier.models.DeliveryTask;
    import com.podzilla.courier.repositories.DeliveryTaskRepository;
    import org.springframework.data.util.Pair;
    import org.springframework.stereotype.Service;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class DeliveryTaskService {

        private final DeliveryTaskRepository deliveryTaskRepository;

        public DeliveryTaskService(DeliveryTaskRepository deliveryTaskRepository) {
            this.deliveryTaskRepository = deliveryTaskRepository;
        }

        public DeliveryTaskResponseDto createDeliveryTask(CreateDeliveryTaskRequestDto deliveryTaskRequestDto) {
            DeliveryTask deliveryTask = DeliveryTaskMapper.toEntity(deliveryTaskRequestDto);
            deliveryTaskRepository.save(deliveryTask);
            return DeliveryTaskMapper.toResponseDto(deliveryTask);
        }

        public List<DeliveryTaskResponseDto> getAllDeliveryTasks() {
            return deliveryTaskRepository.findAll()
                    .stream()
                    .map(DeliveryTaskMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        public Optional<DeliveryTaskResponseDto> getDeliveryTaskById(String id) {
            Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
            return deliveryTask.map(DeliveryTaskMapper::toResponseDto);
        }

        public List<DeliveryTaskResponseDto> getDeliveryTasksByCourierId(String courierId) {
            return deliveryTaskRepository.findByCourierId(courierId)
                    .stream()
                    .map(DeliveryTaskMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        public List<DeliveryTaskResponseDto> getDeliveryTasksByStatus(DeliveryStatus status) {
            return deliveryTaskRepository.findByStatus(status)
                    .stream()
                    .map(DeliveryTaskMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        public List<DeliveryTaskResponseDto> getDeliveryTasksByOrderId(String orderId) {
            return deliveryTaskRepository.findByOrderId(orderId)
                    .stream()
                    .map(DeliveryTaskMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        public Optional<DeliveryTaskResponseDto> updateDeliveryTaskStatus(String id, DeliveryStatus status) {
            Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findById(id);
            if (updatedDeliveryTask.isPresent()) {
                updatedDeliveryTask.get().setStatus(status);
                deliveryTaskRepository.save(updatedDeliveryTask.get());
                return Optional.of(DeliveryTaskMapper.toResponseDto(updatedDeliveryTask.get()));
            }
            return Optional.empty();
        }

        public Pair<Double, Double> getDeliveryTaskLocation(String id) {
            Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
            if (deliveryTask.isPresent()) {
                Double latitude = deliveryTask.get().getCourierLatitude();
                Double longitude = deliveryTask.get().getCourierLongitude();
                return Pair.of(latitude, longitude);
            }

            return Pair.of(0.0, 0.0);
        }

        public DeliveryTaskResponseDto updateDeliveryTaskLocation(String id, Double latitude, Double longitude) {
            Optional<DeliveryTask> updatedDeliveryTask = deliveryTaskRepository.findById(id);
            if (updatedDeliveryTask.isPresent()) {
                DeliveryTask deliveryTask = updatedDeliveryTask.get();
                deliveryTask.setCourierLongitude(latitude);
                deliveryTask.setCourierLongitude(longitude);
                deliveryTaskRepository.save(deliveryTask);
                return DeliveryTaskMapper.toResponseDto(deliveryTask);
            }

            return null;
        }

        public DeliveryTaskResponseDto deleteDeliveryTask(String id) {
            Optional<DeliveryTask> deliveryTask = deliveryTaskRepository.findById(id);
            if (deliveryTask.isPresent()) {
                deliveryTaskRepository.delete(deliveryTask.get());
                return DeliveryTaskMapper.toResponseDto(deliveryTask.get());
            }
            return null;
        }
    }