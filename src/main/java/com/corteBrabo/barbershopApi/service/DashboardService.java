package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Membership;
import com.corteBrabo.barbershopApi.database.model.MembershipPayment;
import com.corteBrabo.barbershopApi.database.model.MembershipStatus;
import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.ScheduleSource;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.MembershipPaymentRepository;
import com.corteBrabo.barbershopApi.database.repository.MembershipRepository;
import com.corteBrabo.barbershopApi.database.repository.ScheduleRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.DashboardDTO;
import com.corteBrabo.barbershopApi.mapper.ScheduleMapper;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@org.springframework.stereotype.Service
public class DashboardService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPaymentRepository paymentRepository;
    private final ScheduleMapper scheduleMapper;

    public DashboardService(ScheduleRepository scheduleRepository,
                            UserRepository userRepository,
                            MembershipRepository membershipRepository,
                            MembershipPaymentRepository paymentRepository,
                            ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.paymentRepository = paymentRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Transactional(readOnly = true)
    public DashboardDTO summary(User currentUser, LocalDate from, LocalDate to) {
        Long businessId = currentUser.getBusinessId();
        boolean owner = currentUser.getRole() == UserRole.OWNER;
        long days = ChronoUnit.DAYS.between(from, to) + 1;
        LocalDate prevFrom = from.minusDays(days);
        LocalDate prevTo = from.minusDays(1);

        List<Schedule> current = load(currentUser, from, to);
        List<Schedule> previous = load(currentUser, prevFrom, prevTo);

        List<Schedule> active = current.stream().filter(s -> s.getStatus() != ScheduleStatus.CANCELADO).toList();
        List<Schedule> done = current.stream().filter(s -> s.getStatus() == ScheduleStatus.CONCLUIDO).toList();
        long noShows = current.stream().filter(s -> s.getStatus() == ScheduleStatus.FALTOU).count();
        long canceled = current.size() - active.size();

        BigDecimal revenue = total(done);
        BigDecimal previousRevenue = total(previous.stream().filter(s -> s.getStatus() == ScheduleStatus.CONCLUIDO).toList());
        long previousAppointments = previous.stream().filter(s -> s.getStatus() != ScheduleStatus.CANCELADO).count();

        double noShowRate = done.size() + noShows == 0 ? 0 : (double) noShows / (done.size() + noShows);
        BigDecimal averageTicket = done.isEmpty() ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(done.size()), 2, RoundingMode.HALF_UP);

        long newClients = owner ? userRepository.countByBusiness_IdAndRoleAndDateBetween(businessId, UserRole.CLIENT, from, to) : 0;

        long activeMembers = 0;
        BigDecimal mrr = BigDecimal.ZERO;
        BigDecimal received = BigDecimal.ZERO;
        if (owner) {
            List<Membership> memberships = membershipRepository.findByBusiness_IdOrderByCreatedAtDesc(businessId).stream()
                    .filter(m -> m.getStatus() != MembershipStatus.CANCELED).toList();
            activeMembers = memberships.stream().filter(m -> m.getEffectiveStatus() == MembershipStatus.ACTIVE).count();
            mrr = memberships.stream().map(m -> m.getPlan().getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
            received = paymentRepository.findByMembership_Business_IdAndPaidAtBetween(businessId, from.atStartOfDay(), to.plusDays(1).atStartOfDay())
                    .stream().map(MembershipPayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        Map<LocalDate, BigDecimal> revenueByDay = new LinkedHashMap<>();
        Map<LocalDate, Long> countByDay = new LinkedHashMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            revenueByDay.put(d, BigDecimal.ZERO);
            countByDay.put(d, 0L);
        }
        done.forEach(s -> revenueByDay.merge(s.getStartAt().toLocalDate(), s.getTotalPrice(), BigDecimal::add));
        active.forEach(s -> countByDay.merge(s.getStartAt().toLocalDate(), 1L, Long::sum));
        List<DashboardDTO.DayPoint> series = revenueByDay.entrySet().stream()
                .map(e -> new DashboardDTO.DayPoint(e.getKey(), e.getValue(), countByDay.get(e.getKey())))
                .toList();

        Map<Long, DashboardDTO.ServiceStat> serviceStats = new LinkedHashMap<>();
        for (Schedule s : active) {
            for (Service svc : s.getServices()) {
                serviceStats.merge(svc.getServiceId(),
                        new DashboardDTO.ServiceStat(svc.getServiceId(), svc.getServiceName(), 1,
                                s.getStatus() == ScheduleStatus.CONCLUIDO && s.getMembership() == null ? svc.getPrice() : BigDecimal.ZERO),
                        (a, b) -> new DashboardDTO.ServiceStat(a.id(), a.name(), a.count() + 1, a.revenue().add(b.revenue())));
            }
        }
        List<DashboardDTO.ServiceStat> topServices = serviceStats.values().stream()
                .sorted(Comparator.comparingLong(DashboardDTO.ServiceStat::count).reversed())
                .limit(5).toList();

        Map<Long, DashboardDTO.ProfessionalStat> proStats = new LinkedHashMap<>();
        for (Schedule s : active) {
            User pro = s.getProfessional();
            BigDecimal value = s.getStatus() == ScheduleStatus.CONCLUIDO ? s.getTotalPrice() : BigDecimal.ZERO;
            BigDecimal pct = pro.getCommissionPercent() == null ? BigDecimal.ZERO : pro.getCommissionPercent();
            BigDecimal commission = value.multiply(pct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            proStats.merge(pro.getId(),
                    new DashboardDTO.ProfessionalStat(pro.getId(), pro.getName(), 1, value, commission),
                    (a, b) -> new DashboardDTO.ProfessionalStat(a.id(), a.name(), a.appointments() + 1,
                            a.revenue().add(b.revenue()), a.commission().add(b.commission())));
        }
        List<DashboardDTO.ProfessionalStat> professionals = new ArrayList<>(proStats.values());
        professionals.sort(Comparator.comparing(DashboardDTO.ProfessionalStat::revenue).reversed());

        LocalDate today = LocalDate.now();
        List<Schedule> todays = Objects.equals(from, today) && Objects.equals(to, today) ? current : load(currentUser, today, today);

        return new DashboardDTO(
                from, to, revenue, previousRevenue, active.size(), previousAppointments,
                done.size(), noShows, noShowRate, canceled, averageTicket, newClients,
                activeMembers, mrr, received,
                active.stream().filter(s -> s.getSource() == ScheduleSource.ONLINE).count(),
                series, topServices, professionals,
                todays.stream().filter(s -> s.getStatus() != ScheduleStatus.CANCELADO).map(scheduleMapper::toResponseDTO).toList()
        );
    }

    private List<Schedule> load(User currentUser, LocalDate from, LocalDate to) {
        var start = from.atStartOfDay();
        var end = to.plusDays(1).atStartOfDay();
        return currentUser.getRole() == UserRole.OWNER
                ? scheduleRepository.findByBusiness_IdAndStartAtBetweenOrderByStartAtAsc(currentUser.getBusinessId(), start, end)
                : scheduleRepository.findByBusiness_IdAndProfessional_IdAndStartAtBetweenOrderByStartAtAsc(
                        currentUser.getBusinessId(), currentUser.getId(), start, end);
    }

    private static BigDecimal total(List<Schedule> schedules) {
        return schedules.stream().map(Schedule::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
