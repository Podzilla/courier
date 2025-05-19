package com.podzilla.courier.controllers;

import com.podzilla.courier.dtos.couriers.CourierResponseDto;
import com.podzilla.courier.dtos.couriers.UpdateCourierRequestDto;
import com.podzilla.courier.services.courier.CourierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CourierController.class);

    public CourierController(final CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping
    @Operation(summary = "Get all couriers",
            description = "Retrieves a list of all couriers.")
    @ApiResponse(responseCode = "200",
            description = "Successfully retrieved list of couriers")
    public ResponseEntity<List<CourierResponseDto>> getAllCouriers() {
        LOGGER.info("Received request to get all couriers");
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
            @PathVariable final String id) {
        LOGGER.info("Received request to get courier with id {}", id);
        return courierService.getCourierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
            @PathVariable final String id,
            @RequestBody(description = "Updated courier details")
            @org.springframework.web.bind.annotation.RequestBody final UpdateCourierRequestDto courier) {
        LOGGER.info("Received request to update courier with id {}", id);
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
            @PathVariable final String id) {
        LOGGER.info("Received request to delete courier with id {}", id);
        return courierService.deleteCourier(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
