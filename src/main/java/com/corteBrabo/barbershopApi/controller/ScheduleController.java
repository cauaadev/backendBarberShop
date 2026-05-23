package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.ScheduleRequestDTO;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import com.corteBrabo.barbershopApi.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADM', 'BARBER')")
    public ResponseEntity<ScheduleResponseDTO> createSchedule(@RequestBody @Valid ScheduleRequestDTO dto) {
        return ResponseEntity.ok(scheduleService.createSchedule(dto));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> findAll(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) ScheduleStatus status) {
        return ResponseEntity.ok(scheduleService.findAllForUser(currentUser, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getById(@PathVariable Long id,
                                                       @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id, currentUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADM')")
    public ResponseEntity<ScheduleResponseDTO> update(@PathVariable Long id,
                                                      @RequestBody @Valid ScheduleRequestDTO dto) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADM', 'BARBER')")
    public ResponseEntity<ScheduleResponseDTO> changeStatus(@PathVariable Long id,
                                                            @RequestParam ScheduleStatus status,
                                                            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(scheduleService.changeStatus(id, status, currentUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADM')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
