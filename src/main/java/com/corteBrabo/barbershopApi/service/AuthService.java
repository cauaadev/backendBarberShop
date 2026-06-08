package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.LoginRequestDTO;
import com.corteBrabo.barbershopApi.dto.LoginResponseDTO;
import com.corteBrabo.barbershopApi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.net.ConnectException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getTelefone(), dto.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Telefone ou senha incorretos", e);
        }

        User user = userRepository.findByTelefone(dto.getTelefone())
                .orElseThrow(() -> new IllegalStateException(
                        "Usuário autenticado mas não encontrado: " + dto.getTelefone()));

        String token = jwtService.generateToken(user);
        return new LoginResponseDTO(token, user.getId(), user.getName(), user.getRole());
    }
}
