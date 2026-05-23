package com.corteBrabo.barbershopApi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceResponseDTO {
    private Long serviceId;
    private String serviceName;
    private double price;
    private String description;
}
