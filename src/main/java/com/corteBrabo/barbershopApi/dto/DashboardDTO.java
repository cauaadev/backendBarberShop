package com.corteBrabo.barbershopApi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardDTO(
        LocalDate from,
        LocalDate to,
        BigDecimal revenue,
        BigDecimal previousRevenue,
        long appointments,
        long previousAppointments,
        long completed,
        long noShows,
        double noShowRate,
        long canceled,
        BigDecimal averageTicket,
        long newClients,
        long activeMembers,
        BigDecimal membershipMrr,
        BigDecimal membershipReceived,
        long onlineBookings,
        List<DayPoint> series,
        List<ServiceStat> topServices,
        List<ProfessionalStat> professionals,
        List<ScheduleResponseDTO> today
) {
    public record DayPoint(LocalDate date, BigDecimal revenue, long appointments) {
    }

    public record ServiceStat(Long id, String name, long count, BigDecimal revenue) {
    }

    public record ProfessionalStat(Long id, String name, long appointments, BigDecimal revenue, BigDecimal commission) {
    }
}
