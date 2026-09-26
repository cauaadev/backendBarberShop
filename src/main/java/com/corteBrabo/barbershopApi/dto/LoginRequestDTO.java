package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Informe seu e-mail ou telefone")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        String password
) {
}
