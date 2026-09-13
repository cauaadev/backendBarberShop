package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.UserRole;

public record LoginResponseDTO(String token, Long userId, String name, UserRole role) {
}
