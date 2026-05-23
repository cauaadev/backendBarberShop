package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.dto.ScheduleRequestDTO;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import com.corteBrabo.barbershopApi.service.ScheduleService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ScheduleResponseDTO> createSchedule(@RequestBody @Valid ScheduleRequestDTO schReqDto) {
        return ResponseEntity.ok(scheduleService.createSchedule(schReqDto));
    }

    @GetMapping("/getSchedule/barber/{barberName}")
    public ResponseEntity<List<ScheduleResponseDTO>> getSchedulesByBarberName(@PathVariable String barberName) {
        return ResponseEntity.ok(scheduleService.getSchedulesByBarberName(barberName));
    }

    @GetMapping("/getSchedule/{id}")
    public ResponseEntity<ScheduleResponseDTO> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> updateSchedule(@PathVariable Long id,
                                                              @RequestBody @Valid ScheduleRequestDTO dto) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
