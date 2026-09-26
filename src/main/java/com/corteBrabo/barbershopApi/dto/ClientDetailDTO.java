package com.corteBrabo.barbershopApi.dto;

import java.util.List;

public record ClientDetailDTO(ClientResponseDTO client, List<ScheduleResponseDTO> history) {
}
