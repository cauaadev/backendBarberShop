package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MembershipRequestDTO(
        @NotNull(message = "Cliente é obrigatório")
        Long clientId,

        @NotNull(message = "Plano é obrigatório")
        Long planId,

        LocalDate startDate,

        boolean paidNow,

        String paymentMethod
) {
}
