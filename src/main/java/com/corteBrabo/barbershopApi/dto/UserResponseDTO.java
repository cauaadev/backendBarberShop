package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.UserRole;

public record UserResponseDTO(
        Long id,
        String name,
        String telefone,
        UserRole role
) {
}
