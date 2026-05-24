package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.dto.LoginRequestDTO;
import com.corteBrabo.barbershopApi.dto.LoginResponseDTO;
import com.corteBrabo.barbershopApi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
