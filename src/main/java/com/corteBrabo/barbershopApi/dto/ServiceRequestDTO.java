package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceRequestDTO(@NotBlank(message = "Nome do serviço é obrigatório")
                                @Size(min = 2, max = 100)
                                String name,
                                @NotNull(message = "Preço é obrigatório")
                                @DecimalMin(value = "0", message = "Preço não pode ser negativo")
                                BigDecimal price,
                                @Min(value = 5, message = "Duração mínima de 5 minutos")
                                @Max(value = 600, message = "Duração máxima de 10 horas")
                                int durationMinutes,
                                @Size(max = 255)
                                String description,
                                Boolean active
) { }
