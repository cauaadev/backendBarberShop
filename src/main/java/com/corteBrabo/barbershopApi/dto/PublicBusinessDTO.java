package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.BusinessSegment;

import java.util.List;

public record PublicBusinessDTO(
        String name,
        String slug,
        BusinessSegment segment,
        String description,
        String address,
        String city,
        String phone,
        String brandColor,
        boolean bookingEnabled,
        List<ServiceResponseDTO> services,
        List<Professional> professionals,
        List<BusinessHoursDTO> hours
) {
    public record Professional(Long id, String name) {
    }
}
