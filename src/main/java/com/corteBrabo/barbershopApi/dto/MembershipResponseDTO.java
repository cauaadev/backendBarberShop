package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MembershipResponseDTO(
        Long id,
        Long clientId,
        String clientName,
        String clientTelefone,
        Long planId,
        String planName,
        BigDecimal price,
        MembershipStatus status,
        LocalDate startedAt,
        LocalDate nextChargeDate,
        int creditsUsed,
        Integer creditsRemaining,
        Integer monthlyCredits
) {
}
