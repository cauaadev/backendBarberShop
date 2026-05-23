package com.corteBrabo.barbershopApi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ScheduleResponseDTO {
    private Long idSchedule;
    private String clientName;
    private List<String> barberNames;
    private List<String> serviceNames;
    private String status;
    private LocalDateTime date;
}
