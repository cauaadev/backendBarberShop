package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.LoginRequestDTO;
import com.corteBrabo.barbershopApi.dto.LoginResponseDTO;
import com.corteBrabo.barbershopApi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

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
        } catch (Exception e) {
            throw new BadCredentialsException("Telefone ou senha incorretos");
        }

        User user = userRepository.findByTelefone(dto.getTelefone())
                .orElseThrow(() -> new BadCredentialsException("Telefone ou senha incorretos"));

        String token = jwtService.generateToken(user);
        return new LoginResponseDTO(token, user.getId(), user.getName(), user.getRole());
    }
}
