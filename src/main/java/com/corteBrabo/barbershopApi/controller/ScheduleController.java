package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.ScheduleRequestDTO;
import com.corteBrabo.barbershopApi.dto.ScheduleResponseDTO;
import com.corteBrabo.barbershopApi.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> findRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long professionalId,
            @RequestParam(required = false) ScheduleStatus status) {
        if (to.isBefore(from) || from.plusDays(92).isBefore(to)) {
            throw new IllegalStateException("Intervalo de datas inválido (máximo de 3 meses)");
        }
        return ResponseEntity.ok(scheduleService.findRange(user, from, to, professionalId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> getById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(user, id));
    }

    @PostMapping
    public ResponseEntity<ScheduleResponseDTO> create(@AuthenticationPrincipal User user, @RequestBody @Valid ScheduleRequestDTO dto) {
        return ResponseEntity.ok(scheduleService.create(user, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDTO> update(@AuthenticationPrincipal User user, @PathVariable Long id,
                                                      @RequestBody @Valid ScheduleRequestDTO dto) {
        return ResponseEntity.ok(scheduleService.update(user, id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ScheduleResponseDTO> changeStatus(@AuthenticationPrincipal User user, @PathVariable Long id,
                                                            @RequestParam ScheduleStatus status) {
        return ResponseEntity.ok(scheduleService.changeStatus(user, id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        scheduleService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
