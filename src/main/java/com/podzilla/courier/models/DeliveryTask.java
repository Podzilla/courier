package com.podzilla.courier.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "delivery_tasks")
public class DeliveryTask {
    @Id
    private String id;
    private String orderId;
    private String courierId;
    private Double price;
    private DeliveryStatus status;
    private Double orderLatitude;
    private Double orderLongitude;
    private Double currentLatitude;
    private Double currentLongitude;
    private String otp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeliveryTask() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.currentLatitude = 0.0; // warehouse lat
        this.currentLongitude = 0.0; // warehouse long
    }
}