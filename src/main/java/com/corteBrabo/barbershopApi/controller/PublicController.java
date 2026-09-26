package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.dto.AvailabilityDTO;
import com.corteBrabo.barbershopApi.dto.PublicBookingRequestDTO;
import com.corteBrabo.barbershopApi.dto.PublicBookingResponseDTO;
import com.corteBrabo.barbershopApi.dto.PublicBusinessDTO;
import com.corteBrabo.barbershopApi.service.PublicBookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/public/{slug}")
public class PublicController {

    private final PublicBookingService bookingService;

    public PublicController(PublicBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<PublicBusinessDTO> business(@PathVariable String slug) {
        return ResponseEntity.ok(bookingService.getBusiness(slug));
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityDTO> availability(
            @PathVariable String slug,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam List<Long> serviceIds,
            @RequestParam(required = false) Long professionalId) {
        return ResponseEntity.ok(bookingService.availability(slug, date, serviceIds, professionalId));
    }

    @PostMapping("/bookings")
    public ResponseEntity<PublicBookingResponseDTO> book(@PathVariable String slug, @RequestBody @Valid PublicBookingRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.book(slug, dto));
    }
}
