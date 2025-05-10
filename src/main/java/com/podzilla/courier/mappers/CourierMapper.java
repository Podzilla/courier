package com.podzilla.courier.mappers;

import com.podzilla.courier.dtos.couriers.CourierResponseDto;
import com.podzilla.courier.dtos.couriers.CreateCourierRequestDto;
import com.podzilla.courier.dtos.couriers.UpdateCourierRequestDto;
import com.podzilla.courier.models.Courier;

public class CourierMapper {

    public static Courier toEntity(CreateCourierRequestDto dto) {
        Courier courier = new Courier();
        courier.setName(dto.getName());
        courier.setMobileNo(dto.getMobileNo());
        return courier;
    }

    public static Courier toEntity(UpdateCourierRequestDto dto) {
        Courier courier = new Courier();
        courier.setId(dto.getId());
        if (dto.getName() != null) courier.setName(dto.getName());
        if (dto.getMobileNo() != null) courier.setMobileNo(dto.getMobileNo());
        if (dto.getStatus() != null) courier.setStatus(dto.getStatus());
        return courier;
    }

    public static CourierResponseDto toCreateResponseDto(Courier courier) {
        return new CourierResponseDto(courier.getId(), courier.getName(), courier.getMobileNo());
    }
}
