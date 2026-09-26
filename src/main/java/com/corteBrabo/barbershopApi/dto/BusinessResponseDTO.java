package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.BusinessPlan;
import com.corteBrabo.barbershopApi.database.model.BusinessSegment;
import com.corteBrabo.barbershopApi.database.model.SubscriptionStatus;

import java.time.LocalDateTime;

public record BusinessResponseDTO(
        Long id,
        String name,
        String slug,
        BusinessSegment segment,
        String phone,
        String email,
        String address,
        String city,
        String description,
        String brandColor,
        boolean bookingEnabled,
        int slotIntervalMinutes,
        int minAdvanceMinutes,
        BusinessPlan plan,
        SubscriptionStatus subscriptionStatus,
        LocalDateTime trialEndsAt,
        long trialDaysLeft
) {
}
