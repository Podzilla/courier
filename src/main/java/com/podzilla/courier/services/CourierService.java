package com.podzilla.courier.services;

import com.podzilla.courier.dtos.couriers.*;
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
    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    public List<CourierResponseDto> getAllCouriers() {
        logger.info("Fetching all couriers");
        List<CourierResponseDto> couriers = courierRepository.findAll().stream()
                .map(CourierMapper::toCreateResponseDto).collect(Collectors.toList());
        logger.info("Couriers Fetched: {}", couriers);
        return couriers;
    }

    public Optional<CourierResponseDto> getCourierById(String id) {
        logger.info("Fetching courier with ID: {}", id);
        Optional<Courier> courier = courierRepository.findById(id);
        if (courier.isPresent()) {
            logger.debug("Courier found with ID: {}", courier.get().getId());
        } else {
            logger.debug("Courier not found with ID: {}", id);
        }
        return courier.map(CourierMapper::toCreateResponseDto);
    }

    public CourierResponseDto createCourier(CreateCourierRequestDto courier) {
        logger.info("Creating new courier");
        Courier newCourier = CourierMapper.toEntity(courier);
        Courier savedCourier = courierRepository.save(newCourier);
        logger.info("Created courier with ID: {}", savedCourier.getId());
        return CourierMapper.toCreateResponseDto(savedCourier);
    }

    public Optional<CourierResponseDto> updateCourier(String id, UpdateCourierRequestDto courierDto) {
        logger.info("Updating courier with ID: {}", id);
        Optional<Courier> existingCourier = courierRepository.findById(id);
        if (existingCourier.isEmpty()) {
            logger.debug("Courier not found with ID: {}", id);
            return Optional.empty();
        }
        Courier courier = existingCourier.get();
        if (courierDto.getName() != null) courier.setName(courierDto.getName());
        if (courierDto.getMobileNo() != null) courier.setMobileNo(courierDto.getMobileNo());
        if (courierDto.getStatus() != null) courier.setStatus(courierDto.getStatus());
        Courier savedCourier = courierRepository.save(courier);
        logger.info("Updated courier with ID: {}", savedCourier.getId());
        return Optional.of(CourierMapper.toCreateResponseDto(savedCourier));
    }

    public Optional<CourierResponseDto> deleteCourier(String id) {
        logger.info("Deleting courier with ID: {}", id);
        Optional<Courier> courier = courierRepository.findById(id);
        if (courier.isPresent()) {
            courierRepository.deleteById(id);
            logger.info("Deleted courier with ID: {}", id);
            return courier.map(CourierMapper::toCreateResponseDto);
        }
        logger.debug("Courier not found with ID: {}", id);
        return Optional.empty();
    }
}
