package com.corteBrabo.barbershopApi.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class ScheduleResponseDTO {
    private Long idSchedule;
    private String clientName;
    private String barberName;
    private List<String> serviceNames;
    private String status;
    private LocalDateTime date;

}
