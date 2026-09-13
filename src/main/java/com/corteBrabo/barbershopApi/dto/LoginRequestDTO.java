package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "Senha é obrigatória")
        String password
) {
}
