package com.podzilla.courier.dtos.couriers;

import com.podzilla.courier.models.CourierStatus;

public record CourierResponseDto(String id, String name, String mobileNo, CourierStatus status) {
}
