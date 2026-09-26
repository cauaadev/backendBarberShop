package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100)
        String name,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos numéricos")
        String telefone,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email
) {
}
