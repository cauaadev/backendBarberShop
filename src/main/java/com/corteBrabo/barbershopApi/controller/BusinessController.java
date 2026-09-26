package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.BillingResponseDTO;
import com.corteBrabo.barbershopApi.dto.BusinessHoursDTO;
import com.corteBrabo.barbershopApi.dto.BusinessResponseDTO;
import com.corteBrabo.barbershopApi.dto.BusinessUpdateDTO;
import com.corteBrabo.barbershopApi.dto.PlanChangeDTO;
import com.corteBrabo.barbershopApi.service.BusinessService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping
    public ResponseEntity<BusinessResponseDTO> get(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(businessService.get(user));
    }

    @PutMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BusinessResponseDTO> update(@AuthenticationPrincipal User user, @RequestBody @Valid BusinessUpdateDTO dto) {
        return ResponseEntity.ok(businessService.update(user, dto));
    }

    @GetMapping("/hours")
    public ResponseEntity<List<BusinessHoursDTO>> hours(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(businessService.getHours(user.getBusinessId()));
    }

    @PutMapping("/hours")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<BusinessHoursDTO>> updateHours(@AuthenticationPrincipal User user,
                                                              @RequestBody @Valid List<BusinessHoursDTO> hours) {
        return ResponseEntity.ok(businessService.updateHours(user, hours));
    }

    @GetMapping("/billing")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BillingResponseDTO> billing(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(businessService.billing(user));
    }

    @PutMapping("/billing/plan")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BillingResponseDTO> changePlan(@AuthenticationPrincipal User user, @RequestBody @Valid PlanChangeDTO dto) {
        return ResponseEntity.ok(businessService.changePlan(user, dto.plan()));
    }
}
