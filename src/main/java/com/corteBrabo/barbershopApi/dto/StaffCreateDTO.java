package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.UserRole;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffCreateDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos numéricos")
    private String telefone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String password;

    @NotNull(message = "Cargo é obrigatório")
    private UserRole role;

    @AssertTrue(message = "Funcionário deve ser BARBER ou ADM")
    public boolean isValidStaffRole() {
        return role == UserRole.BARBER || role == UserRole.ADM;
    }
}
