package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record MembershipPlanRequestDTO(
        @NotBlank(message = "Nome do plano é obrigatório")
        @Size(min = 2, max = 100)
        String name,

        @Size(max = 500)
        String description,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "1", message = "Preço deve ser maior que zero")
        BigDecimal price,

        @Min(value = 1, message = "Informe ao menos 1 uso por mês ou deixe ilimitado")
        Integer monthlyCredits,

        @NotEmpty(message = "Escolha os serviços incluídos")
        List<Long> serviceIds,

        Boolean active
) {
}
