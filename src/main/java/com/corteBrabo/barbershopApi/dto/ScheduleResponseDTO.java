package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ScheduleResponseDTO {
    private Long scheduleId;
    private Long clientId;
    private String clientName;
    private List<String> barberNames;
    private List<String> serviceNames;
    private ScheduleStatus status;
    private LocalDateTime date;
}
