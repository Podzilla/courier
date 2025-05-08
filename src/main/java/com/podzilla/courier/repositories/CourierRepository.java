package com.podzilla.courier.repositories;

import com.podzilla.courier.models.Courier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

public interface CourierRepository extends MongoRepository<Courier, String> {

}
