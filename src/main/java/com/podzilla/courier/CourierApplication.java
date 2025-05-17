package com.podzilla.courier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.podzilla.courier.repositories")
@SpringBootApplication
@ComponentScan(basePackages = {"com.podzilla.courier", "com.podzilla.mq"})
public class CourierApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CourierApplication.class, args);
    }

}
