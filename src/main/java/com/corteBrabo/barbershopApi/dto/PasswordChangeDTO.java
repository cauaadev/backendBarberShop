package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeDTO(
        @NotBlank(message = "Informe a senha atual")
        String currentPassword,

        @NotBlank(message = "Informe a nova senha")
        @Size(min = 8, max = 100, message = "Senha deve ter pelo menos 8 caracteres")
        String newPassword
) {
}
