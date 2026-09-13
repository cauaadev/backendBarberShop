package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ScheduleResponseDTO(
        Long scheduleId,
        Long clientId,
        String clientName,
        List<String> barberNames,
        List<String> serviceNames,
        ScheduleStatus status,
        LocalDateTime date) {
}
