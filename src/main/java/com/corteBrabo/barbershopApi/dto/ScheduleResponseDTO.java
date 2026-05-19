package com.corteBrabo.barbershopApi.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class ScheduleResponseDTO {
    private Long id;
    private String clientNome;
    private String barberNome;
    private List<String> serviceNomes;
    private String status;
    private LocalDateTime date;

}
