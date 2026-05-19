package com.corteBrabo.barbershopApi.mapper;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.dto.ScheduleRequestDTO;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    ScheduleRequestDTO toDto(Schedule sch);
    ScheduleResponseDTO toResponseDTO(Schedule sch);
    Schedule toEntity(ScheduleRequestDTO schDto);

}
