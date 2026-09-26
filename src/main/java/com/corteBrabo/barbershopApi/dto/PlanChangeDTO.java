package com.corteBrabo.barbershopApi.dto;

import com.corteBrabo.barbershopApi.database.model.BusinessPlan;
import jakarta.validation.constraints.NotNull;

public record PlanChangeDTO(@NotNull(message = "Plano é obrigatório") BusinessPlan plan) {
}
