package com.corteBrabo.barbershopApi.dto;

public record ServiceResponseDTO(
        Long serviceId,
        String serviceName,
        double price,
        String description
) {
}
