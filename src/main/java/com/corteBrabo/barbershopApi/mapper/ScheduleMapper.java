package com.corteBrabo.barbershopApi.mapper;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "barbers", target = "barberNames", qualifiedByName = "barbersToNames")
    @Mapping(source = "services", target = "serviceNames", qualifiedByName = "servicesToNames")
    ScheduleResponseDTO toResponseDTO(Schedule sch);

    @Named("barbersToNames")
    static List<String> barbersToNames(List<User> barbers) {
        if (barbers == null) return List.of();
        return barbers.stream().map(User::getName).toList();
    }

    @Named("servicesToNames")
    static List<String> servicesToNames(List<Service> services) {
        if (services == null) return List.of();
        return services.stream().map(Service::getServiceName).toList();
    }
}
