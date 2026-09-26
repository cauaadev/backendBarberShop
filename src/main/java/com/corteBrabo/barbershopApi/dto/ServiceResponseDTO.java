package com.corteBrabo.barbershopApi.dto;

import java.math.BigDecimal;

public record ServiceResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        int durationMinutes,
        String description,
        boolean active
) {
}
