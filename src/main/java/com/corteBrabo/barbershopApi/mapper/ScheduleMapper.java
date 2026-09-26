package com.corteBrabo.barbershopApi.mapper;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ServiceMapper.class)
public interface ScheduleMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "client.telefone", target = "clientTelefone")
    @Mapping(source = "professional.id", target = "professionalId")
    @Mapping(source = "professional.name", target = "professionalName")
    @Mapping(target = "coveredByMembership", expression = "java(sch.getMembership() != null)")
    ScheduleResponseDTO toResponseDTO(Schedule sch);
}
