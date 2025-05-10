package com.podzilla.courier.controllers;

import com.podzilla.courier.dtos.couriers.*;
import com.podzilla.courier.services.CourierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;
    private static final Logger logger =
            LoggerFactory.getLogger(CourierController.class);

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping
    @Operation(summary = "Get all couriers",
            description = "Retrieves a list of all couriers.")
    @ApiResponse(responseCode = "200",
            description = "Successfully retrieved list of couriers")
    public ResponseEntity<List<CourierResponseDto>> getAllCouriers() {
        logger.info("Received request to get all couriers");
        return ResponseEntity.ok(courierService.getAllCouriers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get courier by ID",
            description = "Retrieves a specific courier by their unique ID.")
    @ApiResponse(responseCode = "200",
            description = "Courier found and returned")
    @ApiResponse(responseCode = "404",
            description = "Courier not found")
    public ResponseEntity<CourierResponseDto> getCourierById(
            @Parameter(description = "ID of the courier to retrieve")
            @PathVariable String id) {
        logger.info("Received request to get courier with id {}", id);
        return courierService.getCourierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new courier",
            description = "Adds a new courier to the system.")
    @ApiResponse(responseCode = "200",
            description = "Courier successfully created")
    public ResponseEntity<CourierResponseDto> createCourier(
            @RequestBody(description = "Details of the courier to create")
            @org.springframework.web.bind.annotation.RequestBody
            CreateCourierRequestDto courier) {
        logger.info("Received request to add courier");
        return ResponseEntity.ok(courierService.createCourier(courier));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a courier",
            description = "Updates details of an existing courier.")
    @ApiResponse(responseCode = "200",
            description = "Courier updated successfully")
    @ApiResponse(responseCode = "404",
            description = "Courier not found")
    public ResponseEntity<CourierResponseDto> updateCourier(
            @Parameter(description = "ID of the courier to update")
            @PathVariable String id,
            @RequestBody(description = "Updated courier details")
            @org.springframework.web.bind.annotation.RequestBody
            UpdateCourierRequestDto courier) {
        logger.info("Received request to update courier with id {}", id);
        return courierService.updateCourier(id, courier)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a courier",
            description = "Removes a courier from the system.")
    @ApiResponse(responseCode = "200",
            description = "Courier deleted successfully")
    @ApiResponse(responseCode = "404",
            description = "Courier not found")
    public ResponseEntity<CourierResponseDto> deleteCourier(
            @Parameter(description = "ID of the courier to delete")
            @PathVariable String id) {
        logger.info("Received request to delete courier with id {}", id);
        return courierService.deleteCourier(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}