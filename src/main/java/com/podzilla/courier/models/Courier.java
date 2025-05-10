package com.podzilla.courier.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data @Document(collection = "couriers")
public class Courier {
    @Id private String id;
    private String name;
    private String status;
    private String mobileNo;
    private Double rating;
}