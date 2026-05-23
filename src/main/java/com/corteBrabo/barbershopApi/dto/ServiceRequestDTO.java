package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRequestDTO {

    @NotBlank(message = "Nome do serviço é obrigatório")
    @Size(min = 2, max = 100)
    private String serviceName;

    @Positive(message = "Preço deve ser positivo")
    private double price;

    @Size(max = 255)
    private String description;
}
