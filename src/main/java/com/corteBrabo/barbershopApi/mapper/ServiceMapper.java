package com.corteBrabo.barbershopApi.mapper;

import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.dto.ServiceRequestDTO;
import com.corteBrabo.barbershopApi.dto.ServiceResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceResponseDTO toResponseDTO(Service service);
    Service toEntity(ServiceRequestDTO dto);
    void updateEntityFromDto(ServiceRequestDTO dto, @MappingTarget Service service);
}
