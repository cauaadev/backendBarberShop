package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Membership;
import com.corteBrabo.barbershopApi.database.model.MembershipStatus;
import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.ScheduleSource;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.MembershipRepository;
import com.corteBrabo.barbershopApi.database.repository.ScheduleRepository;
import com.corteBrabo.barbershopApi.database.repository.ServiceRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.ScheduleRequestDTO;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.ScheduleMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final MembershipRepository membershipRepository;
    private final BusinessService businessService;
    private final ScheduleMapper mapper;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           UserRepository userRepository,
                           ServiceRepository serviceRepository,
                           MembershipRepository membershipRepository,
                           BusinessService businessService,
                           ScheduleMapper mapper) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.membershipRepository = membershipRepository;
        this.businessService = businessService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDTO> findRange(User currentUser, LocalDate from, LocalDate to,
                                               Long professionalId, ScheduleStatus status) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        List<Schedule> base = professionalId == null
                ? scheduleRepository.findByBusiness_IdAndStartAtBetweenOrderByStartAtAsc(currentUser.getBusinessId(), start, end)
                : scheduleRepository.findByBusiness_IdAndProfessional_IdAndStartAtBetweenOrderByStartAtAsc(
                        currentUser.getBusinessId(), professionalId, start, end);
        return base.stream()
                .filter(s -> status == null || s.getStatus() == status)
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDTO getById(User currentUser, Long id) {
        return mapper.toResponseDTO(load(currentUser, id));
    }

    @Transactional
    public ScheduleResponseDTO create(User currentUser, ScheduleRequestDTO dto) {
        Schedule sch = new Schedule();
        sch.setBusiness(businessService.load(currentUser.getBusinessId()));
        sch.setStatus(ScheduleStatus.CONFIRMADO);
        sch.setSource(ScheduleSource.INTERNO);
        apply(currentUser, sch, dto);
        return mapper.toResponseDTO(scheduleRepository.save(sch));
    }

    @Transactional
    public ScheduleResponseDTO update(User currentUser, Long id, ScheduleRequestDTO dto) {
        Schedule sch = load(currentUser, id);
        ensureCanModify(sch, currentUser);
        releaseMembershipCredit(sch);
        apply(currentUser, sch, dto);
        return mapper.toResponseDTO(scheduleRepository.save(sch));
    }

    @Transactional
    public ScheduleResponseDTO changeStatus(User currentUser, Long id, ScheduleStatus newStatus) {
        Schedule sch = load(currentUser, id);
        ensureCanModify(sch, currentUser);
        if (newStatus == ScheduleStatus.CANCELADO) {
            releaseMembershipCredit(sch);
        }
        sch.setStatus(newStatus);
        return mapper.toResponseDTO(scheduleRepository.save(sch));
    }

    @Transactional
    public void delete(User currentUser, Long id) {
        Schedule sch = load(currentUser, id);
        releaseMembershipCredit(sch);
        scheduleRepository.delete(sch);
    }

    public Schedule book(User client, User professional, List<Service> services, LocalDateTime startAt,
                         String notes, ScheduleSource source, ScheduleStatus status, Long ignoreId) {
        int duration = services.stream().mapToInt(Service::getDurationMinutes).sum();
        LocalDateTime endAt = startAt.plusMinutes(duration);

        boolean conflict = scheduleRepository.findOverlapping(List.of(professional.getId()), ScheduleStatus.BLOCKING, startAt, endAt)
                .stream().anyMatch(s -> !Objects.equals(s.getId(), ignoreId));
        if (conflict) {
            throw new IllegalStateException(professional.getName() + " já tem um atendimento nesse horário");
        }

        Schedule sch = new Schedule();
        sch.setBusiness(client.getBusiness());
        sch.setClient(client);
        sch.setProfessional(professional);
        sch.setServices(services);
        sch.setStartAt(startAt);
        sch.setEndAt(endAt);
        sch.setTotalPrice(sum(services));
        sch.setNotes(blankToNull(notes));
        sch.setSource(source);
        sch.setStatus(status);
        return scheduleRepository.save(sch);
    }

    private void apply(User currentUser, Schedule sch, ScheduleRequestDTO dto) {
        Long businessId = currentUser.getBusinessId();
        User client = userRepository.findByIdAndBusiness_Id(dto.clientId(), businessId)
                .filter(u -> u.getRole() == UserRole.CLIENT)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        User professional = userRepository.findByIdAndBusiness_Id(dto.professionalId(), businessId)
                .filter(User::isBookable)
                .orElseThrow(() -> new NotFoundException("Profissional não encontrado"));
        if (currentUser.getRole() == UserRole.PROFESSIONAL && !professional.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Você só pode agendar na sua própria agenda");
        }

        List<Service> services = loadServices(businessId, dto.serviceIds());
        int duration = services.stream().mapToInt(Service::getDurationMinutes).sum();
        LocalDateTime endAt = dto.startAt().plusMinutes(duration);

        boolean conflict = scheduleRepository.findOverlapping(List.of(professional.getId()), ScheduleStatus.BLOCKING, dto.startAt(), endAt)
                .stream().anyMatch(s -> !Objects.equals(s.getId(), sch.getId()));
        if (conflict) {
            throw new IllegalStateException(professional.getName() + " já tem um atendimento nesse horário");
        }

        sch.setClient(client);
        sch.setProfessional(professional);
        sch.setServices(services);
        sch.setStartAt(dto.startAt());
        sch.setEndAt(endAt);
        sch.setNotes(blankToNull(dto.notes()));
        sch.setTotalPrice(sum(services));
        sch.setMembership(null);

        if (dto.useMembership()) {
            Membership membership = membershipRepository.findFirstByClient_IdAndStatusNot(client.getId(), MembershipStatus.CANCELED)
                    .filter(m -> m.getEffectiveStatus() == MembershipStatus.ACTIVE)
                    .orElseThrow(() -> new IllegalStateException("Cliente não tem assinatura ativa"));
            var covered = new HashSet<>(membership.getPlan().getServices().stream().map(Service::getServiceId).toList());
            if (!services.stream().allMatch(s -> covered.contains(s.getServiceId()))) {
                throw new IllegalStateException("O plano do cliente não cobre todos os serviços escolhidos");
            }
            Integer remaining = membership.getCreditsRemaining();
            if (remaining != null && remaining <= 0) {
                throw new IllegalStateException("O cliente já usou todos os créditos do mês");
            }
            membership.setCreditsUsed(membership.getCreditsUsed() + 1);
            sch.setMembership(membership);
            sch.setTotalPrice(BigDecimal.ZERO);
        }
    }

    private void releaseMembershipCredit(Schedule sch) {
        Membership membership = sch.getMembership();
        if (membership != null && sch.getStatus() != ScheduleStatus.CANCELADO) {
            membership.setCreditsUsed(Math.max(0, membership.getCreditsUsed() - 1));
            sch.setMembership(null);
        }
    }

    private List<Service> loadServices(Long businessId, List<Long> ids) {
        List<Service> services = serviceRepository.findByBusiness_IdAndServiceIdIn(businessId, ids);
        if (services.size() != new HashSet<>(ids).size()) {
            throw new NotFoundException("Algum serviço não foi encontrado");
        }
        return services;
    }

    private Schedule load(User currentUser, Long id) {
        return scheduleRepository.findByIdAndBusiness_Id(id, currentUser.getBusinessId())
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));
    }

    private void ensureCanModify(Schedule sch, User currentUser) {
        if (currentUser.getRole() == UserRole.OWNER) return;
        if (sch.getProfessional().getId().equals(currentUser.getId())) return;
        throw new AccessDeniedException("Você só pode alterar seus próprios atendimentos");
    }

    private static BigDecimal sum(List<Service> services) {
        return services.stream().map(Service::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
