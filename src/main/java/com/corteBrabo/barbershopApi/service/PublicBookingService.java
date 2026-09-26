package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Business;
import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.ScheduleSource;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.model.SubscriptionStatus;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.BusinessRepository;
import com.corteBrabo.barbershopApi.database.repository.ServiceRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.AvailabilityDTO;
import com.corteBrabo.barbershopApi.dto.PublicBookingRequestDTO;
import com.corteBrabo.barbershopApi.dto.PublicBookingResponseDTO;
import com.corteBrabo.barbershopApi.dto.PublicBusinessDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.ServiceMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

@org.springframework.stereotype.Service
public class PublicBookingService {

    private static final int MAX_DAYS_AHEAD = 60;

    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AvailabilityService availabilityService;
    private final BusinessService businessService;
    private final ScheduleService scheduleService;
    private final ServiceMapper serviceMapper;

    public PublicBookingService(BusinessRepository businessRepository,
                                ServiceRepository serviceRepository,
                                UserRepository userRepository,
                                AvailabilityService availabilityService,
                                BusinessService businessService,
                                ScheduleService scheduleService,
                                ServiceMapper serviceMapper) {
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.availabilityService = availabilityService;
        this.businessService = businessService;
        this.scheduleService = scheduleService;
        this.serviceMapper = serviceMapper;
    }

    @Transactional(readOnly = true)
    public PublicBusinessDTO getBusiness(String slug) {
        Business business = load(slug);
        return new PublicBusinessDTO(
                business.getName(),
                business.getSlug(),
                business.getSegment(),
                business.getDescription(),
                business.getAddress(),
                business.getCity(),
                business.getPhone(),
                business.getBrandColor(),
                isBookable(business),
                serviceRepository.findByBusiness_IdAndActiveTrueOrderByServiceNameAsc(business.getId()).stream()
                        .map(serviceMapper::toResponseDTO).toList(),
                userRepository.findByBusiness_IdAndBookableTrueAndActiveTrueOrderByNameAsc(business.getId()).stream()
                        .map(u -> new PublicBusinessDTO.Professional(u.getId(), u.getName())).toList(),
                businessService.getHours(business.getId())
        );
    }

    @Transactional(readOnly = true)
    public AvailabilityDTO availability(String slug, LocalDate date, List<Long> serviceIds, Long professionalId) {
        Business business = load(slug);
        validateDate(date);
        List<Service> services = loadServices(business, serviceIds);
        int duration = services.stream().mapToInt(Service::getDurationMinutes).sum();
        return new AvailabilityDTO(date, duration,
                availabilityService.slots(business, date, duration, professionals(business, professionalId)));
    }

    @Transactional
    public PublicBookingResponseDTO book(String slug, PublicBookingRequestDTO dto) {
        Business business = load(slug);
        if (!isBookable(business)) {
            throw new IllegalStateException("Agendamento online indisponível no momento");
        }
        LocalDate date = dto.startAt().toLocalDate();
        validateDate(date);

        List<Service> services = loadServices(business, dto.serviceIds());
        int duration = services.stream().mapToInt(Service::getDurationMinutes).sum();
        String time = dto.startAt().format(DateTimeFormatter.ofPattern("HH:mm"));

        AvailabilityDTO.Slot slot = availabilityService.slots(business, date, duration, professionals(business, dto.professionalId()))
                .stream().filter(s -> s.time().equals(time)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Esse horário acabou de ser ocupado. Escolha outro."));

        User professional = userRepository.lockById(slot.professionalIds().get(0)).orElseThrow();
        User client = findOrCreateClient(business, dto.name(), dto.telefone());

        Schedule sch = scheduleService.book(client, professional, services, dto.startAt(), dto.notes(),
                ScheduleSource.ONLINE, ScheduleStatus.CONFIRMADO, null);

        return new PublicBookingResponseDTO(
                sch.getId(),
                business.getName(),
                business.getPhone(),
                professional.getName(),
                services.stream().map(Service::getServiceName).toList(),
                sch.getStartAt(),
                sch.getEndAt(),
                sch.getTotalPrice()
        );
    }

    private User findOrCreateClient(Business business, String name, String telefone) {
        return userRepository.findByBusiness_IdAndTelefone(business.getId(), telefone)
                .orElseGet(() -> {
                    User client = new User();
                    client.setBusiness(business);
                    client.setName(name.trim());
                    client.setTelefone(telefone);
                    client.setRole(UserRole.CLIENT);
                    return userRepository.save(client);
                });
    }

    private List<User> professionals(Business business, Long professionalId) {
        List<User> all = userRepository.findByBusiness_IdAndBookableTrueAndActiveTrueOrderByNameAsc(business.getId());
        if (professionalId == null) return all;
        List<User> one = all.stream().filter(u -> u.getId().equals(professionalId)).toList();
        if (one.isEmpty()) throw new NotFoundException("Profissional não encontrado");
        return one;
    }

    private List<Service> loadServices(Business business, List<Long> ids) {
        if (ids == null || ids.isEmpty()) throw new IllegalStateException("Escolha ao menos um serviço");
        List<Service> services = serviceRepository.findByBusiness_IdAndServiceIdIn(business.getId(), ids).stream()
                .filter(Service::isActive).toList();
        if (services.size() != new HashSet<>(ids).size()) {
            throw new NotFoundException("Algum serviço não está disponível");
        }
        return services;
    }

    private static void validateDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.isBefore(today) || date.isAfter(today.plusDays(MAX_DAYS_AHEAD))) {
            throw new IllegalStateException("Escolha uma data entre hoje e os próximos " + MAX_DAYS_AHEAD + " dias");
        }
    }

    private static boolean isBookable(Business business) {
        boolean trialValid = business.getSubscriptionStatus() != SubscriptionStatus.TRIAL
                || business.getTrialEndsAt() == null
                || business.getTrialEndsAt().isAfter(LocalDateTime.now());
        return business.isBookingEnabled() && trialValid && business.getSubscriptionStatus() != SubscriptionStatus.CANCELED;
    }

    private Business load(String slug) {
        return businessRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Página não encontrada"));
    }
}
