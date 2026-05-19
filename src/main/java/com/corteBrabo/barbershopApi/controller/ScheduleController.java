package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.Service;
import com.corteBrabo.barbershopApi.database.repository.ScheduleRepository;
import com.corteBrabo.barbershopApi.dto.ScheduleRequestDTO;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import com.corteBrabo.barbershopApi.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }


    @PostMapping("/createSchedule")
    public ResponseEntity<String> createSchedule(@RequestBody @Valid ScheduleRequestDTO schReqDto) {
        scheduleService.createSchedule(schReqDto);
        return ResponseEntity.ok("Criado com sucesso!");
    }


    @GetMapping("getSchedule/barber/{barberName}")
    public ScheduleResponseDTO getScheduleByBarberName(@PathVariable String barberName) {
        return scheduleService.getScheduleByBarberName(barberName);
    }

    @GetMapping("getSchedule/{id}")
    public ResponseEntity<ScheduleResponseDTO> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }



}
