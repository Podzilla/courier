package com.podzilla.courier.services;

import com.podzilla.courier.models.Courier;
import com.podzilla.courier.repositories.courier.CourierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourierService {
    private final CourierRepository courierRepository;

    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    public List<Courier> getAllCouriers() {
        return courierRepository.findAll();
    }

    public Optional<Courier> getCourierById(String id) {
        return courierRepository.findById(id);
    }

    public Courier addCourier(Courier courier) {
        Courier savedCourier = courierRepository.save(courier);
        return savedCourier;
    }

    public Optional<Courier> updateCourier(String id, Courier courier) {
        if (courierRepository.findById(id).isEmpty())
            return Optional.empty();
        courier.setId(id);
        return Optional.of(courierRepository.save(courier));
    }

    public Optional<Courier> deleteCourier(String id) {
        Optional<Courier> courier = courierRepository.findById(id);
        courierRepository.deleteById(id);
        return courier;
    }
}
