package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.dto.ServiceRequestDTO;
import com.corteBrabo.barbershopApi.dto.ServiceResponseDTO;
import com.corteBrabo.barbershopApi.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponseDTO>> findAll() {
        return ResponseEntity.ok(serviceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADM')")
    public ResponseEntity<ServiceResponseDTO> create(@RequestBody @Valid ServiceRequestDTO dto) {
        return ResponseEntity.ok(serviceService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADM')")
    public ResponseEntity<ServiceResponseDTO> update(@PathVariable Long id,
                                                     @RequestBody @Valid ServiceRequestDTO dto) {
        return ResponseEntity.ok(serviceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADM')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
