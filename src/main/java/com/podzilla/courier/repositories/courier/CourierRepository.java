package com.podzilla.courier.repositories.courier;

import com.podzilla.courier.models.Courier;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourierRepository extends MongoRepository<Courier, String> {

}
