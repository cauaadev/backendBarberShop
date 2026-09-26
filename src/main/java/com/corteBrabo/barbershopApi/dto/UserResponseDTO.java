package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.UserRole;

import java.math.BigDecimal;

public record UserResponseDTO(
        Long id,
        String name,
        String telefone,
        String email,
        UserRole role,
        boolean bookable,
        boolean active,
        BigDecimal commissionPercent
) {
}
