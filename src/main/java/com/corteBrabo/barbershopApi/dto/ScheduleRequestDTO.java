package com.corteBrabo.barbershopApi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class ScheduleRequestDTO {

    @NotBlank(message = "Nome do cliente é obrigatório")
    private String clientName;

    @NotBlank(message = "Nome do barbeiro é obrigatório")
    private String barberName;

    @NotEmpty(message = "Informe ao menos um serviço")
    private List<Long> serviceIds;

    @NotNull(message = "Data é obrigatória")
    @Future(message = "O agendamento deve ser em uma data futura")
    private LocalDateTime date;

 }