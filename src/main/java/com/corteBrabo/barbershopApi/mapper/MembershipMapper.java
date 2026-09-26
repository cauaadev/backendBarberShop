package com.corteBrabo.barbershopApi.mapper;

import com.corteBrabo.barbershopApi.database.model.Membership;
import com.corteBrabo.barbershopApi.database.model.MembershipPlan;
import com.corteBrabo.barbershopApi.dto.MembershipPlanResponseDTO;
import com.corteBrabo.barbershopApi.dto.MembershipResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ServiceMapper.class)
public interface MembershipMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "client.telefone", target = "clientTelefone")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.name", target = "planName")
    @Mapping(source = "plan.price", target = "price")
    @Mapping(source = "plan.monthlyCredits", target = "monthlyCredits")
    @Mapping(source = "effectiveStatus", target = "status")
    MembershipResponseDTO toResponseDTO(Membership membership);

    MembershipPlanResponseDTO toPlanResponseDTO(MembershipPlan plan, long activeMembers);
}
