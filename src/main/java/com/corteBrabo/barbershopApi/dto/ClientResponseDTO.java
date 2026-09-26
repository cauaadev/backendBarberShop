package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.MembershipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientResponseDTO(
        Long id,
        String name,
        String telefone,
        String notes,
        LocalDate createdAt,
        long visits,
        long noShows,
        BigDecimal totalSpent,
        LocalDateTime lastVisit,
        LocalDateTime nextVisit,
        String membershipPlanName,
        MembershipStatus membershipStatus
) {
}
