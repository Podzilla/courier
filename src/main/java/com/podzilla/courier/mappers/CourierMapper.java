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

    public static CourierResponseDto toCreateResponseDto(Courier courier) {
        return new CourierResponseDto(courier.getId(), courier.getName(), courier.getMobileNo(), courier.getStatus());
    }
}
