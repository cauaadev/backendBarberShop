package com.corteBrabo.barbershopApi.dto;

public record LoginResponseDTO(String token, UserResponseDTO user, BusinessResponseDTO business) {
}
