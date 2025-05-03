package com.podzilla.courier.controllers;

import com.podzilla.courier.models.Courier;
import com.podzilla.courier.services.CourierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Courier>> getAllCouriers() {
        return ResponseEntity.ok(courierService.getAllCouriers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Courier> getCourierById(@PathVariable String id) {
        return courierService.getCourierById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<Courier> addCourier(@RequestBody Courier courier) {
        if (courier.getId() == null || courier.getName() == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(courierService.addCourier(courier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Courier> updateCourier(@PathVariable String id, @RequestBody Courier courier) {
        return courierService.updateCourier(id, courier).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Courier> deleteCourier(@PathVariable String id) {
        return courierService.deleteCourier(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
