package com.corteBrabo.barbershopApi.controller;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.dto.LoginRequestDTO;
import com.corteBrabo.barbershopApi.dto.LoginResponseDTO;
import com.corteBrabo.barbershopApi.dto.SignupRequestDTO;
import com.corteBrabo.barbershopApi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDTO> signup(@RequestBody @Valid SignupRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.me(user));
    }
}
