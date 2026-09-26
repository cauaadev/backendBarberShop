package com.corteBrabo.barbershopApi.database.model;

import java.util.List;

public enum ScheduleStatus {
    PENDENTE,
    CONFIRMADO,
    CANCELADO,
    CONCLUIDO,
    FALTOU;

    public static final List<ScheduleStatus> BLOCKING = List.of(PENDENTE, CONFIRMADO, CONCLUIDO);
}
