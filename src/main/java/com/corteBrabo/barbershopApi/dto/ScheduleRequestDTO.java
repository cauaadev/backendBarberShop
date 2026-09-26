package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record ScheduleRequestDTO (
        @NotNull(message = "Cliente é obrigatório")
        Long clientId,

        @NotNull(message = "Profissional é obrigatório")
        Long professionalId,

        @NotEmpty(message = "Informe ao menos um serviço")
        List<Long> serviceIds,

        @NotNull(message = "Data é obrigatória")
        LocalDateTime startAt,

        @Size(max = 500)
        String notes,

        boolean useMembership
){}
