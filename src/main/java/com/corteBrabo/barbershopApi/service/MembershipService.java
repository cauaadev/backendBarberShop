package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Business;
import com.corteBrabo.barbershopApi.database.model.Membership;
import com.corteBrabo.barbershopApi.database.model.MembershipPayment;
import com.corteBrabo.barbershopApi.database.model.MembershipPlan;
import com.corteBrabo.barbershopApi.database.model.MembershipStatus;
import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.model.SubscriptionStatus;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.MembershipPaymentRepository;
import com.corteBrabo.barbershopApi.database.repository.MembershipPlanRepository;
import com.corteBrabo.barbershopApi.database.repository.MembershipRepository;
import com.corteBrabo.barbershopApi.database.repository.ServiceRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.MembershipPaymentRequestDTO;
import com.corteBrabo.barbershopApi.dto.MembershipPlanRequestDTO;
import com.corteBrabo.barbershopApi.dto.MembershipPlanResponseDTO;
import com.corteBrabo.barbershopApi.dto.MembershipRequestDTO;
import com.corteBrabo.barbershopApi.dto.MembershipResponseDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.exception.PlanLimitException;
import com.corteBrabo.barbershopApi.mapper.MembershipMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class MembershipService {

    private final MembershipPlanRepository planRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPaymentRepository paymentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final BusinessService businessService;
    private final MembershipMapper mapper;

    public MembershipService(MembershipPlanRepository planRepository,
                             MembershipRepository membershipRepository,
                             MembershipPaymentRepository paymentRepository,
                             ServiceRepository serviceRepository,
                             UserRepository userRepository,
                             BusinessService businessService,
                             MembershipMapper mapper) {
        this.planRepository = planRepository;
        this.membershipRepository = membershipRepository;
        this.paymentRepository = paymentRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.businessService = businessService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<MembershipPlanResponseDTO> findPlans(User currentUser) {
        Long businessId = currentUser.getBusinessId();
        Map<Long, Long> activeByPlan = membershipRepository.findByBusiness_IdOrderByCreatedAtDesc(businessId).stream()
                .filter(m -> m.getStatus() != MembershipStatus.CANCELED)
                .collect(Collectors.groupingBy(m -> m.getPlan().getId(), Collectors.counting()));
        return planRepository.findByBusiness_IdOrderByPriceAsc(businessId).stream()
                .map(p -> mapper.toPlanResponseDTO(p, activeByPlan.getOrDefault(p.getId(), 0L)))
                .toList();
    }

    @Transactional
    public MembershipPlanResponseDTO createPlan(User currentUser, MembershipPlanRequestDTO dto) {
        Business business = ensureEnabled(currentUser);
        MembershipPlan plan = new MembershipPlan();
        plan.setBusiness(business);
        applyPlan(plan, dto, business.getId());
        return mapper.toPlanResponseDTO(planRepository.save(plan), 0);
    }

    @Transactional
    public MembershipPlanResponseDTO updatePlan(User currentUser, Long id, MembershipPlanRequestDTO dto) {
        ensureEnabled(currentUser);
        MembershipPlan plan = loadPlan(currentUser, id);
        applyPlan(plan, dto, currentUser.getBusinessId());
        return mapper.toPlanResponseDTO(planRepository.save(plan), 0);
    }

    @Transactional
    public void deletePlan(User currentUser, Long id) {
        MembershipPlan plan = loadPlan(currentUser, id);
        if (membershipRepository.existsByPlan_Id(id)) {
            plan.setActive(false);
            planRepository.save(plan);
            return;
        }
        planRepository.delete(plan);
    }

    @Transactional(readOnly = true)
    public List<MembershipResponseDTO> findMemberships(User currentUser) {
        return membershipRepository.findByBusiness_IdOrderByCreatedAtDesc(currentUser.getBusinessId()).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public MembershipResponseDTO subscribe(User currentUser, MembershipRequestDTO dto) {
        Business business = ensureEnabled(currentUser);
        User client = userRepository.findByIdAndBusiness_Id(dto.clientId(), business.getId())
                .filter(u -> u.getRole() == UserRole.CLIENT)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));
        MembershipPlan plan = loadPlan(currentUser, dto.planId());
        if (!plan.isActive()) {
            throw new IllegalStateException("Esse plano está desativado");
        }
        if (membershipRepository.findFirstByClient_IdAndStatusNot(client.getId(), MembershipStatus.CANCELED).isPresent()) {
            throw new IllegalStateException("Esse cliente já tem uma assinatura. Cancele a atual antes de trocar.");
        }

        LocalDate start = dto.startDate() == null ? LocalDate.now() : dto.startDate();
        Membership membership = new Membership();
        membership.setBusiness(business);
        membership.setClient(client);
        membership.setPlan(plan);
        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setStartedAt(start);
        membership.setNextChargeDate(dto.paidNow() ? start.plusMonths(1) : start);
        membershipRepository.save(membership);

        if (dto.paidNow()) {
            savePayment(membership, dto.paymentMethod() == null ? "PIX" : dto.paymentMethod());
        }
        return mapper.toResponseDTO(membership);
    }

    @Transactional
    public MembershipResponseDTO registerPayment(User currentUser, Long id, MembershipPaymentRequestDTO dto) {
        Membership membership = load(currentUser, id);
        if (membership.getStatus() == MembershipStatus.CANCELED) {
            throw new IllegalStateException("Assinatura cancelada");
        }
        LocalDate base = membership.getNextChargeDate().isBefore(LocalDate.now()) ? LocalDate.now() : membership.getNextChargeDate();
        membership.setNextChargeDate(base.plusMonths(1));
        membership.setCreditsUsed(0);
        membership.setStatus(MembershipStatus.ACTIVE);
        savePayment(membership, dto.method());
        return mapper.toResponseDTO(membershipRepository.save(membership));
    }

    @Transactional
    public MembershipResponseDTO cancel(User currentUser, Long id) {
        Membership membership = load(currentUser, id);
        membership.setStatus(MembershipStatus.CANCELED);
        membership.setCanceledAt(LocalDateTime.now());
        return mapper.toResponseDTO(membershipRepository.save(membership));
    }

    private void savePayment(Membership membership, String method) {
        MembershipPayment payment = new MembershipPayment();
        payment.setMembership(membership);
        payment.setAmount(membership.getPlan().getPrice());
        payment.setMethod(method);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private void applyPlan(MembershipPlan plan, MembershipPlanRequestDTO dto, Long businessId) {
        List<Service> services = serviceRepository.findByBusiness_IdAndServiceIdIn(businessId, dto.serviceIds());
        if (services.size() != new HashSet<>(dto.serviceIds()).size()) {
            throw new NotFoundException("Algum serviço não foi encontrado");
        }
        plan.setName(dto.name().trim());
        plan.setDescription(dto.description() == null || dto.description().isBlank() ? null : dto.description().trim());
        plan.setPrice(dto.price());
        plan.setMonthlyCredits(dto.monthlyCredits());
        plan.setServices(services);
        if (dto.active() != null) plan.setActive(dto.active());
    }

    private Business ensureEnabled(User currentUser) {
        Business business = businessService.load(currentUser.getBusinessId());
        if (business.getSubscriptionStatus() != SubscriptionStatus.TRIAL && !business.getPlan().isMembershipEnabled()) {
            throw new PlanLimitException("O clube de assinatura está disponível a partir do plano Profissional");
        }
        return business;
    }

    private MembershipPlan loadPlan(User currentUser, Long id) {
        return planRepository.findByIdAndBusiness_Id(id, currentUser.getBusinessId())
                .orElseThrow(() -> new NotFoundException("Plano não encontrado"));
    }

    private Membership load(User currentUser, Long id) {
        return membershipRepository.findByIdAndBusiness_Id(id, currentUser.getBusinessId())
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada"));
    }
}
