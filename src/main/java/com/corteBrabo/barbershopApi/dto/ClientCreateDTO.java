package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


public record ClientCreateDTO(@NotBlank(message = "Nome é obrigatório")
                              @Size(min = 2, max = 100)
                              String name,
                              @NotBlank(message = "Telefone é obrigatório")
                              @Pattern(regexp = "\\d{10,11}",
                                      message = "Telefone deve ter 10 ou 11 dígitos numéricos")
                              String telefone
) {


}
