package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.UserRole;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record StaffCreateDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100)
        String name,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos numéricos")
        String telefone,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 100, message = "Senha deve ter pelo menos 8 caracteres")
        String password,

        @NotNull(message = "Cargo é obrigatório")
        UserRole role,

        boolean bookable,

        @DecimalMin(value = "0", message = "Comissão não pode ser negativa")
        @DecimalMax(value = "100", message = "Comissão não pode passar de 100%")
        BigDecimal commissionPercent
) {

    @AssertTrue(message = "Membro da equipe deve ser OWNER ou PROFESSIONAL")
    public boolean isValidStaffRole() {
        return role == UserRole.OWNER || role == UserRole.PROFESSIONAL;
    }
}
