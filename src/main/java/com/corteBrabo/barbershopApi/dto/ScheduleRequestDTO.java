package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ScheduleRequestDTO {

    @NotNull(message = "Id do cliente é obrigatório")
    private Long clientId;

    @NotEmpty(message = "Informe ao menos um barbeiro")
    private List<Long> barberIds;

    @NotEmpty(message = "Informe ao menos um serviço")
    private List<Long> serviceIds;

    @NotNull(message = "Data é obrigatória")
    @Future(message = "O agendamento deve ser em uma data futura")
    private LocalDateTime date;
}
