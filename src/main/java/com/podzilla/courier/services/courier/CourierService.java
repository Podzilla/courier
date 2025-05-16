package com.podzilla.courier.services.courier;

import com.podzilla.courier.dtos.couriers.CourierResponseDto;
import com.podzilla.courier.dtos.couriers.CreateCourierRequestDto;
import com.podzilla.courier.dtos.couriers.UpdateCourierRequestDto;
import com.podzilla.courier.mappers.CourierMapper;
import com.podzilla.courier.models.Courier;
import com.podzilla.courier.repositories.courier.CourierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourierService {

    private final CourierRepository courierRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CourierService.class);

    public CourierService(final CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    public List<CourierResponseDto> getAllCouriers() {
        LOGGER.info("Fetching all couriers");
        List<CourierResponseDto> couriers = courierRepository.findAll().stream()
                .map(CourierMapper::toCreateResponseDto).collect(Collectors.toList());
        LOGGER.info("Couriers Fetched: {}", couriers);
        return couriers;
    }

    public Optional<CourierResponseDto> getCourierById(final String id) {
        LOGGER.info("Fetching courier with ID: {}", id);
        Optional<Courier> courier = courierRepository.findById(id);
        if (courier.isPresent()) {
            LOGGER.debug("Courier found with ID: {}", courier.get().getId());
        } else {
            LOGGER.debug("Courier not found with ID: {}", id);
        }
        return courier.map(CourierMapper::toCreateResponseDto);
    }

    public CourierResponseDto createCourier(final CreateCourierRequestDto courier) {
        LOGGER.info("Creating new courier");
        Courier newCourier = CourierMapper.toEntity(courier);
        Courier savedCourier = courierRepository.save(newCourier);
        LOGGER.info("Created courier with ID: {}", savedCourier.getId());
        return CourierMapper.toCreateResponseDto(savedCourier);
    }

    public Optional<CourierResponseDto> updateCourier(final String id, final UpdateCourierRequestDto courierDto) {
        LOGGER.info("Updating courier with ID: {}", id);
        Optional<Courier> existingCourier = courierRepository.findById(id);
        if (existingCourier.isEmpty()) {
            LOGGER.debug("Courier not found with ID: {}", id);
            return Optional.empty();
        }
        Courier courier = existingCourier.get();
        if (courierDto.getName() != null) {
            courier.setName(courierDto.getName());
        }
        if (courierDto.getMobileNo() != null) {
            courier.setMobileNo(courierDto.getMobileNo());
        }
        if (courierDto.getStatus() != null) {
            courier.setStatus(courierDto.getStatus());
        }
        Courier savedCourier = courierRepository.save(courier);
        LOGGER.info("Updated courier with ID: {}", savedCourier.getId());
        return Optional.of(CourierMapper.toCreateResponseDto(savedCourier));
    }

    public Optional<CourierResponseDto> deleteCourier(final String id) {
        LOGGER.info("Deleting courier with ID: {}", id);
        Optional<Courier> courier = courierRepository.findById(id);
        if (courier.isPresent()) {
            courierRepository.deleteById(id);
            LOGGER.info("Deleted courier with ID: {}", id);
            return courier.map(CourierMapper::toCreateResponseDto);
        }
        LOGGER.debug("Courier not found with ID: {}", id);
        return Optional.empty();
    }
}

