package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.BusinessPlan;
import com.corteBrabo.barbershopApi.database.model.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BillingResponseDTO(
        BusinessPlan plan,
        SubscriptionStatus subscriptionStatus,
        LocalDateTime trialEndsAt,
        long trialDaysLeft,
        BigDecimal monthlyPrice,
        long professionalsUsed,
        Integer professionalsLimit,
        List<PlanOption> plans
) {
    public record PlanOption(BusinessPlan plan, BigDecimal monthlyPrice, Integer maxProfessionals, boolean membershipEnabled) {
    }
}
