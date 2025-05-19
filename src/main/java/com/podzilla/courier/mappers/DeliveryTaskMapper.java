package com.podzilla.courier.mappers;

import com.podzilla.courier.dtos.delivery_tasks.CancelDeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.CreateDeliveryTaskRequestDto;
import com.podzilla.courier.dtos.delivery_tasks.DeliveryTaskResponseDto;
import com.podzilla.courier.dtos.delivery_tasks.SubmitCourierRatingResponseDto;
import com.podzilla.courier.models.DeliveryTask;

public class DeliveryTaskMapper {

    public static DeliveryTask toEntity(final CreateDeliveryTaskRequestDto dto) {
        DeliveryTask task = new DeliveryTask();
        task.setOrderId(dto.getOrderId());
        task.setCourierId(dto.getCourierId());
        task.setTotalAmount(dto.getTotalAmount());
        task.setOrderLatitude(dto.getOrderLatitude());
        task.setOrderLongitude(dto.getOrderLongitude());
        task.setConfirmationType(dto.getConfirmationType());
        task.setSignature(dto.getSignature());
        return task;
    }

    public static DeliveryTaskResponseDto toCreateResponseDto(final DeliveryTask task) {
        return new DeliveryTaskResponseDto(
                task.getId(),
                task.getOrderId(),
                task.getCourierId(),
                task.getTotalAmount(),
                task.getStatus(),
                task.getOrderLatitude(),
                task.getOrderLongitude(),
                task.getCourierLatitude(),
                task.getCourierLongitude(),
                task.getConfirmationType()
        );
    }

    public static CancelDeliveryTaskResponseDto toCancelResponseDto(final DeliveryTask task) {
        return new CancelDeliveryTaskResponseDto(
                task.getId(),
                task.getOrderId(),
                task.getCourierId(),
                task.getCancellationReason()
        );
    }

    public static SubmitCourierRatingResponseDto toSubmitCourierRatingResponseDto(final DeliveryTask task) {
        return new SubmitCourierRatingResponseDto(
                task.getId(),
                task.getOrderId(),
                task.getCourierId(),
                task.getCourierRating()
        );
    }
}
