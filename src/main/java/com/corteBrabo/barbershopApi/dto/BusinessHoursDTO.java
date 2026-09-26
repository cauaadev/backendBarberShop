package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalTime;

public record BusinessHoursDTO(
        @Min(1) @Max(7)
        int dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean closed
) {
}
