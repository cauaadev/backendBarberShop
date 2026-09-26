package com.corteBrabo.barbershopApi.dto;

import java.math.BigDecimal;
import java.util.List;

public record MembershipPlanResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer monthlyCredits,
        List<ServiceSummaryDTO> services,
        boolean active,
        long activeMembers
) {
}
