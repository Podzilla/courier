package com.podzilla.courier.models;

import com.podzilla.mq.events.OrderAssignedToCourierEvent.ConfirmationType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "delivery_tasks")
public class DeliveryTask {
    @Id
    private String id;
    private String orderId;
    private String courierId;
    private BigDecimal totalAmount;
    private DeliveryStatus status;
    private Double orderLatitude;
    private Double orderLongitude;
    private Double courierLatitude;
    private Double courierLongitude;
    private String otp;
    private String qrCode;
    private String signature;
    private String cancellationReason;
    private BigDecimal courierRating;
    private LocalDateTime ratingTimestamp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ConfirmationType confirmationType;

    public DeliveryTask() {
        this.status = DeliveryStatus.ASSIGNED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.courierLatitude = 0.0; // warehouse lat
        this.courierLongitude = 0.0; // warehouse long
    }
}
