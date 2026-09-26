package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.BusinessSegment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequestDTO(
        @NotBlank(message = "Nome do negócio é obrigatório")
        @Size(min = 2, max = 120)
        String businessName,

        @NotNull(message = "Segmento é obrigatório")
        BusinessSegment segment,

        @NotBlank(message = "Seu nome é obrigatório")
        @Size(min = 2, max = 100)
        String ownerName,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos numéricos")
        String telefone,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 100, message = "Senha deve ter pelo menos 8 caracteres")
        String password
) {
}
