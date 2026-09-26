package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.ScheduleSource;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ScheduleResponseDTO(
        Long id,
        Long clientId,
        String clientName,
        String clientTelefone,
        Long professionalId,
        String professionalName,
        List<ServiceSummaryDTO> services,
        ScheduleStatus status,
        ScheduleSource source,
        LocalDateTime startAt,
        LocalDateTime endAt,
        BigDecimal totalPrice,
        String notes,
        boolean coveredByMembership) {
}
