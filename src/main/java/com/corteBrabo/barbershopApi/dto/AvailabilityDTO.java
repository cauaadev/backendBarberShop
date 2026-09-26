package com.corteBrabo.barbershopApi.dto;

import java.time.LocalDate;
import java.util.List;

public record AvailabilityDTO(LocalDate date, int durationMinutes, List<Slot> slots) {
    public record Slot(String time, List<Long> professionalIds) {
    }
}
