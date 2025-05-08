package com.podzilla.courier.mappers;

import com.podzilla.courier.dtos.delivery_tasks.CancelDeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.DeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.SubmitCourierRatingResponseDto;
import com.podzilla.courier.models.DeliveryTask;

public class DeliveryTaskMapper {

    public static DeliveryTask toEntity(CreateDeliveryTaskRequestDto dto) {
        DeliveryTask task = new DeliveryTask();
        task.setOrderId(dto.getOrderId());
        task.setCourierId(dto.getCourierId());
        task.setPrice(dto.getPrice());
        task.setOrderLatitude(dto.getOrderLatitude());
        task.setOrderLongitude(dto.getOrderLongitude());
        return task;
    }

    public static DeliveryTaskResponseDto toCreateResponseDto(DeliveryTask task) {
        return new DeliveryTaskResponseDto(
                task.getId(),
                task.getOrderId(),
                task.getCourierId(),
                task.getPrice(),
                task.getStatus(),
                task.getOrderLatitude(),
                task.getOrderLongitude(),
                task.getCourierLatitude(),
                task.getCourierLongitude()
        );
    }

    public static CancelDeliveryTaskResponseDto toCancelResponseDto(DeliveryTask task) {
        return new CancelDeliveryTaskResponseDto(
                task.getId(),
                task.getOrderId(),
                task.getCourierId(),
                task.getCancellationReason()
        );
    }

    public static SubmitCourierRatingResponseDto toSubmitCourierRatingResponseDto(DeliveryTask task) {
        return new SubmitCourierRatingResponseDto(
                task.getId(),
                task.getOrderId(),
                task.getCourierId(),
                task.getCourierRating()
        );
    }
}