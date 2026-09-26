package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.ServiceRequestDTO;
import com.corteBrabo.barbershopApi.dto.ServiceResponseDTO;
import com.corteBrabo.barbershopApi.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> findAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(serviceService.findAll(user));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ServiceResponseDTO> create(@AuthenticationPrincipal User user, @RequestBody @Valid ServiceRequestDTO dto) {
        return ResponseEntity.ok(serviceService.create(user, dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ServiceResponseDTO> update(@AuthenticationPrincipal User user, @PathVariable Long id,
                                                     @RequestBody @Valid ServiceRequestDTO dto) {
        return ResponseEntity.ok(serviceService.update(user, id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        serviceService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
