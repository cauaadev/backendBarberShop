package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record PublicBookingRequestDTO(
        @NotBlank(message = "Informe seu nome")
        @Size(min = 2, max = 100)
        String name,

        @NotBlank(message = "Informe seu WhatsApp")
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
        String telefone,

        @NotEmpty(message = "Escolha ao menos um serviço")
        List<Long> serviceIds,

        Long professionalId,

        @NotNull(message = "Escolha um horário")
        LocalDateTime startAt,

        @Size(max = 500)
        String notes
) {
}
