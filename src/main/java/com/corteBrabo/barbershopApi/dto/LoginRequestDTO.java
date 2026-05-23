package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
