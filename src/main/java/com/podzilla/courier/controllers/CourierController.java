package com.podzilla.courier.controllers;

import com.podzilla.courier.dtos.couriers.*;
import com.podzilla.courier.models.Courier;
import com.podzilla.courier.services.CourierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;
    private static final Logger logger = LoggerFactory.getLogger(CourierController.class);

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping
    public ResponseEntity<List<CourierResponseDto>> getAllCouriers() {
        logger.info("Received request to get all couriers");
        return ResponseEntity.ok(courierService.getAllCouriers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourierResponseDto> getCourierById(@PathVariable String id) {
        logger.info("Received request to get courier with id {}", id);
        return courierService.getCourierById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<CourierResponseDto> createCourier(@RequestBody CreateCourierRequestDto courier) {
        logger.info("Received request to add courier");
        return ResponseEntity.ok(courierService.createCourier(courier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourierResponseDto> updateCourier(@PathVariable String id, @RequestBody UpdateCourierRequestDto courier) {
        logger.info("Received request to update courier with id {}", id);
        return courierService.updateCourier(id, courier).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CourierResponseDto> deleteCourier(@PathVariable String id) {
        logger.info("Received request to delete courier with id {}", id);
        return courierService.deleteCourier(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}