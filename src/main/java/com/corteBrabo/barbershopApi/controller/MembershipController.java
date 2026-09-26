package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.MembershipPaymentRequestDTO;
import com.corteBrabo.barbershopApi.dto.MembershipPlanRequestDTO;
import com.corteBrabo.barbershopApi.dto.MembershipPlanResponseDTO;
import com.corteBrabo.barbershopApi.dto.MembershipRequestDTO;
import com.corteBrabo.barbershopApi.dto.MembershipResponseDTO;
import com.corteBrabo.barbershopApi.service.MembershipService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memberships")
@PreAuthorize("hasRole('OWNER')")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<MembershipPlanResponseDTO>> plans(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(membershipService.findPlans(user));
    }

    @PostMapping("/plans")
    public ResponseEntity<MembershipPlanResponseDTO> createPlan(@AuthenticationPrincipal User user,
                                                                @RequestBody @Valid MembershipPlanRequestDTO dto) {
        return ResponseEntity.ok(membershipService.createPlan(user, dto));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<MembershipPlanResponseDTO> updatePlan(@AuthenticationPrincipal User user, @PathVariable Long id,
                                                                @RequestBody @Valid MembershipPlanRequestDTO dto) {
        return ResponseEntity.ok(membershipService.updatePlan(user, id, dto));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@AuthenticationPrincipal User user, @PathVariable Long id) {
        membershipService.deletePlan(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MembershipResponseDTO>> memberships(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(membershipService.findMemberships(user));
    }

    @PostMapping
    public ResponseEntity<MembershipResponseDTO> subscribe(@AuthenticationPrincipal User user,
                                                           @RequestBody @Valid MembershipRequestDTO dto) {
        return ResponseEntity.ok(membershipService.subscribe(user, dto));
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<MembershipResponseDTO> registerPayment(@AuthenticationPrincipal User user, @PathVariable Long id,
                                                                 @RequestBody @Valid MembershipPaymentRequestDTO dto) {
        return ResponseEntity.ok(membershipService.registerPayment(user, id, dto));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<MembershipResponseDTO> cancel(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(membershipService.cancel(user, id));
    }
}
