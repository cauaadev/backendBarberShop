package com.corteBrabo.barbershopApi.mapper;

import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.dto.ServiceResponseDTO;
import com.corteBrabo.barbershopApi.dto.ServiceSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(source = "serviceId", target = "id")
    @Mapping(source = "serviceName", target = "name")
    ServiceResponseDTO toResponseDTO(Service service);

    @Mapping(source = "serviceId", target = "id")
    @Mapping(source = "serviceName", target = "name")
    ServiceSummaryDTO toSummaryDTO(Service service);

    List<ServiceSummaryDTO> toSummaryDTOs(List<Service> services);
}
