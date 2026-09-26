package com.corteBrabo.barbershopApi.mapper;

import com.corteBrabo.barbershopApi.database.model.Business;
import com.corteBrabo.barbershopApi.database.model.BusinessHours;
import com.corteBrabo.barbershopApi.dto.BusinessHoursDTO;
import com.corteBrabo.barbershopApi.dto.BusinessResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Duration;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface BusinessMapper {

    @Mapping(target = "trialDaysLeft", expression = "java(trialDaysLeft(business))")
    BusinessResponseDTO toResponseDTO(Business business);

    BusinessHoursDTO toHoursDTO(BusinessHours hours);

    default long trialDaysLeft(Business business) {
        if (business.getTrialEndsAt() == null) return 0;
        long hours = Duration.between(LocalDateTime.now(), business.getTrialEndsAt()).toHours();
        return Math.max(0, (long) Math.ceil(hours / 24.0));
    }
}
