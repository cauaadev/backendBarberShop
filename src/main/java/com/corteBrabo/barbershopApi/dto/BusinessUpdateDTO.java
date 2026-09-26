package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.BusinessSegment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BusinessUpdateDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 120)
        String name,

        @NotBlank(message = "Link é obrigatório")
        @Pattern(regexp = "[a-z0-9]+(-[a-z0-9]+)*", message = "Use só letras minúsculas, números e hífen")
        @Size(min = 3, max = 80, message = "O link deve ter entre 3 e 80 caracteres")
        String slug,

        @NotNull(message = "Segmento é obrigatório")
        BusinessSegment segment,

        @Pattern(regexp = "\\d{10,11}|", message = "Telefone deve ter 10 ou 11 dígitos numéricos")
        String phone,

        @Email(message = "E-mail inválido")
        String email,

        @Size(max = 255)
        String address,

        @Size(max = 120)
        String city,

        @Size(max = 500)
        String description,

        @Pattern(regexp = "#[0-9A-Fa-f]{6}", message = "Cor inválida")
        String brandColor,

        boolean bookingEnabled,

        @Min(value = 5, message = "Intervalo mínimo de 5 minutos")
        @Max(value = 120, message = "Intervalo máximo de 120 minutos")
        int slotIntervalMinutes,

        @Min(value = 0, message = "Antecedência não pode ser negativa")
        @Max(value = 10080, message = "Antecedência máxima de 7 dias")
        int minAdvanceMinutes
) {
}
