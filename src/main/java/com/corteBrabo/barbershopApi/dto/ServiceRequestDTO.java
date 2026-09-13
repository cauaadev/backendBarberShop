package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


public record ServiceRequestDTO(@NotBlank(message = "Nome do serviço é obrigatório")
                                @Size(min = 2, max = 100)
                                String serviceName,
                                @Positive(message = "Preço deve ser positivo")
                                double price,
                                @Size(max = 255)
                                String description
) { }
