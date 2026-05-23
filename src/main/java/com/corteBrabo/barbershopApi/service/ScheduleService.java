package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.ScheduleRepository;
import com.corteBrabo.barbershopApi.database.repository.ServiceRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.ScheduleRequestDTO;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.ScheduleMapper;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@org.springframework.stereotype.Service
public class ScheduleService {

    private static final List<ScheduleStatus> ACTIVE_STATUSES =
            List.of(ScheduleStatus.PENDENTE, ScheduleStatus.CONFIRMADO);

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ScheduleMapper mapper;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           UserRepository userRepository,
                           ServiceRepository serviceRepository,
                           ScheduleMapper mapper) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.mapper = mapper;
    }

    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO dto) {
        if (dto.getClientId() == null) {
            throw new IllegalStateException("clientId é obrigatório");
        }
        if (scheduleRepository.existsByClient_IdAndStatusIn(dto.getClientId(), ACTIVE_STATUSES)) {
            throw new IllegalStateException("Cliente já possui um agendamento ativo");
        }

        Schedule sch = new Schedule();
        sch.setClient(loadClient(dto.getClientId()));
        sch.setBarbers(loadBarbers(dto.getBarberIds()));
        sch.setServices(loadServices(dto.getServiceIds()));
        sch.setDate(dto.getDate());
        sch.setStatus(ScheduleStatus.PENDENTE);

        return mapper.toResponseDTO(scheduleRepository.save(sch));
    }

    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new NotFoundException("Agendamento não encontrado");
        }
        scheduleRepository.deleteById(id);
    }

    public ScheduleResponseDTO getScheduleById(Long id, User currentUser) {
        Schedule sch = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));
        ensureCanView(sch, currentUser);
        return mapper.toResponseDTO(sch);
    }

    public List<ScheduleResponseDTO> findAllForUser(User currentUser, ScheduleStatus statusFilter) {
        List<Schedule> base = (currentUser.getRole() == UserRole.ADM)
                ? (statusFilter == null ? scheduleRepository.findAll() : scheduleRepository.findByStatus(statusFilter))
                : scheduleRepository.findByBarbers_Id(currentUser.getId());

        return base.stream()
                .filter(s -> statusFilter == null || s.getStatus() == statusFilter)
                .map(mapper::toResponseDTO)
                .toList();
    }

    public ScheduleResponseDTO updateSchedule(Long id, ScheduleRequestDTO dto) {
        Schedule sch = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id para update não encontrado"));

        sch.setClient(loadClient(dto.getClientId()));
        sch.setBarbers(loadBarbers(dto.getBarberIds()));
        sch.setServices(loadServices(dto.getServiceIds()));
        sch.setDate(dto.getDate());

        return mapper.toResponseDTO(scheduleRepository.save(sch));
    }

    public ScheduleResponseDTO changeStatus(Long id, ScheduleStatus newStatus, User currentUser) {
        Schedule sch = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));

        if (currentUser.getRole() == UserRole.BARBER) {
            boolean isBarberOfThis = sch.getBarbers().stream()
                    .anyMatch(b -> b.getId().equals(currentUser.getId()));
            if (!isBarberOfThis) {
                throw new AccessDeniedException("Você não é barbeiro deste agendamento");
            }
        }

        sch.setStatus(newStatus);
        return mapper.toResponseDTO(scheduleRepository.save(sch));
    }

    private void ensureCanView(Schedule sch, User currentUser) {
        if (currentUser.getRole() == UserRole.ADM) return;
        if (currentUser.getRole() == UserRole.BARBER
                && sch.getBarbers().stream().anyMatch(b -> b.getId().equals(currentUser.getId()))) return;
        throw new AccessDeniedException("Sem permissão para ver este agendamento");
    }

    private User loadClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + clientId));
        if (client.getRole() != UserRole.CLIENT) {
            throw new IllegalStateException("Usuário " + clientId + " não é CLIENT");
        }
        return client;
    }

    private List<User> loadBarbers(List<Long> ids) {
        List<User> barbers = userRepository.findAllById(ids);
        if (barbers.size() != ids.size()) {
            throw new NotFoundException("Algum barbeiro não foi encontrado");
        }
        boolean hasNonBarber = barbers.stream().anyMatch(u -> u.getRole() != UserRole.BARBER);
        if (hasNonBarber) {
            throw new IllegalStateException("Todos os usuários informados devem ter role BARBER");
        }
        return barbers;
    }

    private List<Service> loadServices(List<Long> ids) {
        List<Service> services = serviceRepository.findAllById(ids);
        if (services.size() != ids.size()) {
            throw new NotFoundException("Algum serviço não foi encontrado");
        }
        return services;
    }
}
