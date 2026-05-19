package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import com.corteBrabo.barbershopApi.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getScheduleByUserId(@PathVariable Long id) {
        ScheduleResponseDTO sch = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(sch);
    }
}
