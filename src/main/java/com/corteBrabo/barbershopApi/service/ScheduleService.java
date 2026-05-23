package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Schedule;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ScheduleMapper mapper;

    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO dto) {
        if (scheduleRepository.existsByClient_Id(dto.getClientId())) {
            throw new IllegalStateException("Cliente já possui um agendamento");
        }

        Schedule sch = new Schedule();
        sch.setClient(loadClient(dto.getClientId()));
        sch.setBarbers(loadBarbers(dto.getBarberIds()));
        sch.setServices(loadServices(dto.getServiceIds()));
        sch.setDate(dto.getDate());
        sch.setStatus("PENDENTE");

        return mapper.toResponseDTO(scheduleRepository.save(sch));
    }

    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new NotFoundException("Agendamento não encontrado");
        }
        scheduleRepository.deleteById(id);
    }

    public ScheduleResponseDTO getScheduleById(Long id) {
        Schedule sch = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agendamento não encontrado"));
        return mapper.toResponseDTO(sch);
    }

    public ScheduleResponseDTO updateSchedule(Long id, ScheduleRequestDTO dto) {
        Schedule sch = scheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id para update não encontrado"));

        if (!sch.getClient().getId().equals(dto.getClientId())
                && scheduleRepository.existsByClient_Id(dto.getClientId())) {
            throw new IllegalStateException("Cliente já possui um agendamento");
        }

        sch.setClient(loadClient(dto.getClientId()));
        sch.setBarbers(loadBarbers(dto.getBarberIds()));
        sch.setServices(loadServices(dto.getServiceIds()));
        sch.setDate(dto.getDate());

        return mapper.toResponseDTO(scheduleRepository.save(sch));
    }

    public List<ScheduleResponseDTO> getSchedulesByBarberName(String barberName) {
        return scheduleRepository.findByBarbers_Name(barberName).stream()
                .map(mapper::toResponseDTO)
                .toList();
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
