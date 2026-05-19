package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.repository.ScheduleRepository;
import com.corteBrabo.barbershopApi.dto.ScheduleRequestDTO;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleMapper mapper;

    public ScheduleResponseDTO createSchedule(ScheduleRequestDTO schReqDTO) {
        Schedule sch = mapper.toEntity(schReqDTO);
        sch.setStatus("PENDENTE");
        Schedule salvo = scheduleRepository.save(sch);
        return mapper.toResponseDTO(salvo);
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
    public void updateSchedule(Long id, ScheduleRequestDTO schReqDTO) {
        if(scheduleRepository.existsById(id)){
            Schedule sch = mapper.toEntity(schReqDTO);
            sch.setStatus("PENDENTE");
            scheduleRepository.save(sch);
        } else
            throw new NotFoundException("Id para update não encontrado");
    }
    public ScheduleResponseDTO getScheduleByBarberName(String barberName){
        Schedule fullSchedule = scheduleRepository.findByBarberName(barberName);
        return mapper.toResponseDTO(fullSchedule);

    }
}