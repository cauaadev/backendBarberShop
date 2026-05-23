package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String name;
    private String telefone;
    private UserRole role;
}
