package com.corteBrabo.barbershopApi.config;

import com.corteBrabo.barbershopApi.database.model.BusinessSegment;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.SignupRequestDTO;
import com.corteBrabo.barbershopApi.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    @Value("${app.admin.bootstrap-email:}")
    private String email;

    @Value("${app.admin.bootstrap-telefone:}")
    private String telefone;

    @Value("${app.admin.bootstrap-password:}")
    private String password;

    @Value("${app.admin.bootstrap-name:Admin}")
    private String name;

    @Value("${app.admin.bootstrap-business:Minha Empresa}")
    private String businessName;

    private final UserRepository userRepository;
    private final AuthService authService;

    public AdminSeeder(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        if (email == null || email.isBlank() || password == null || password.isBlank()
                || telefone == null || telefone.isBlank()) {
            return;
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.info("Bootstrap admin: e-mail {} já existe, pulando.", email);
            return;
        }

        authService.signup(new SignupRequestDTO(businessName, BusinessSegment.OUTRO, name, email, telefone, password));
    }
}
