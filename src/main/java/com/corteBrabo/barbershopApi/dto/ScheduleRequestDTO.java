package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record ScheduleRequestDTO (
        @NotNull(message = "Id do cliente é obrigatório")
        Long clientId,

        @NotEmpty(message = "Informe ao menos um barbeiro")
        List<Long> barberIds,

        @NotEmpty(message = "Informe ao menos um serviço")
        List<Long> serviceIds,

        @NotNull(message = "Data é obrigatória")
        @Future(message = "O agendamento deve ser em uma data futura")
        LocalDateTime date
){}