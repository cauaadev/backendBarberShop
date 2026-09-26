package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Business;
import com.corteBrabo.barbershopApi.database.model.BusinessHours;
import com.corteBrabo.barbershopApi.database.model.BusinessPlan;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.BusinessHoursRepository;
import com.corteBrabo.barbershopApi.database.repository.BusinessRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.BillingResponseDTO;
import com.corteBrabo.barbershopApi.dto.BusinessHoursDTO;
import com.corteBrabo.barbershopApi.dto.BusinessResponseDTO;
import com.corteBrabo.barbershopApi.dto.BusinessUpdateDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.exception.PlanLimitException;
import com.corteBrabo.barbershopApi.mapper.BusinessMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessHoursRepository hoursRepository;
    private final UserRepository userRepository;
    private final BusinessMapper mapper;

    public BusinessService(BusinessRepository businessRepository,
                           BusinessHoursRepository hoursRepository,
                           UserRepository userRepository,
                           BusinessMapper mapper) {
        this.businessRepository = businessRepository;
        this.hoursRepository = hoursRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public BusinessResponseDTO get(User currentUser) {
        return mapper.toResponseDTO(load(currentUser.getBusinessId()));
    }

    @Transactional
    public BusinessResponseDTO update(User currentUser, BusinessUpdateDTO dto) {
        Business business = load(currentUser.getBusinessId());

        if (!business.getSlug().equals(dto.slug()) && businessRepository.existsBySlug(dto.slug())) {
            throw new IllegalStateException("Esse link já está em uso por outro negócio");
        }

        business.setName(dto.name().trim());
        business.setSlug(dto.slug());
        business.setSegment(dto.segment());
        business.setPhone(blankToNull(dto.phone()));
        business.setEmail(blankToNull(dto.email()));
        business.setAddress(blankToNull(dto.address()));
        business.setCity(blankToNull(dto.city()));
        business.setDescription(blankToNull(dto.description()));
        if (dto.brandColor() != null) business.setBrandColor(dto.brandColor());
        business.setBookingEnabled(dto.bookingEnabled());
        business.setSlotIntervalMinutes(dto.slotIntervalMinutes());
        business.setMinAdvanceMinutes(dto.minAdvanceMinutes());
        return mapper.toResponseDTO(businessRepository.save(business));
    }

    @Transactional(readOnly = true)
    public List<BusinessHoursDTO> getHours(Long businessId) {
        return hoursRepository.findByBusiness_IdOrderByDayOfWeekAsc(businessId).stream()
                .map(mapper::toHoursDTO)
                .toList();
    }

    @Transactional
    public List<BusinessHoursDTO> updateHours(User currentUser, List<BusinessHoursDTO> hours) {
        Business business = load(currentUser.getBusinessId());
        for (BusinessHoursDTO dto : hours) {
            if (!dto.closed() && (dto.openTime() == null || dto.closeTime() == null
                    || !dto.openTime().isBefore(dto.closeTime()))) {
                throw new IllegalStateException("Horário inválido para o dia " + dto.dayOfWeek());
            }
            BusinessHours entity = hoursRepository.findByBusiness_IdAndDayOfWeek(business.getId(), dto.dayOfWeek())
                    .orElseGet(() -> {
                        BusinessHours h = new BusinessHours();
                        h.setBusiness(business);
                        h.setDayOfWeek(dto.dayOfWeek());
                        return h;
                    });
            entity.setOpenTime(dto.openTime());
            entity.setCloseTime(dto.closeTime());
            entity.setClosed(dto.closed());
            hoursRepository.save(entity);
        }
        return getHours(business.getId());
    }

    @Transactional(readOnly = true)
    public BillingResponseDTO billing(User currentUser) {
        Business business = load(currentUser.getBusinessId());
        BusinessPlan plan = business.getPlan();
        List<BillingResponseDTO.PlanOption> options = Arrays.stream(BusinessPlan.values())
                .map(p -> new BillingResponseDTO.PlanOption(p, p.getMonthlyPrice(), limitOf(p), p.isMembershipEnabled()))
                .toList();
        return new BillingResponseDTO(
                plan,
                business.getSubscriptionStatus(),
                business.getTrialEndsAt(),
                mapper.trialDaysLeft(business),
                plan.getMonthlyPrice(),
                userRepository.countByBusiness_IdAndBookableTrueAndActiveTrue(business.getId()),
                limitOf(plan),
                options
        );
    }

    @Transactional
    public BillingResponseDTO changePlan(User currentUser, BusinessPlan plan) {
        Business business = load(currentUser.getBusinessId());
        long professionals = userRepository.countByBusiness_IdAndBookableTrueAndActiveTrue(business.getId());
        if (professionals > plan.getMaxProfessionals()) {
            throw new PlanLimitException("Esse plano permite até " + plan.getMaxProfessionals()
                    + " profissional(is). Desative profissionais antes de mudar.");
        }
        business.setPlan(plan);
        businessRepository.save(business);
        return billing(currentUser);
    }

    public Business load(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new NotFoundException("Negócio não encontrado"));
    }

    private static Integer limitOf(BusinessPlan plan) {
        return plan.getMaxProfessionals() == Integer.MAX_VALUE ? null : plan.getMaxProfessionals();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
