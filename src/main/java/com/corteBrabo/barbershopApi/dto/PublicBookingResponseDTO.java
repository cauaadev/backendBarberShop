package com.corteBrabo.barbershopApi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PublicBookingResponseDTO(
        Long id,
        String businessName,
        String businessPhone,
        String professionalName,
        List<String> services,
        LocalDateTime startAt,
        LocalDateTime endAt,
        BigDecimal totalPrice
) {
}
