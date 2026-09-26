package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.config.ServiceSeeder;
import com.corteBrabo.barbershopApi.database.model.Business;
import com.corteBrabo.barbershopApi.database.model.BusinessPlan;
import com.corteBrabo.barbershopApi.database.model.SubscriptionStatus;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.BusinessRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.LoginRequestDTO;
import com.corteBrabo.barbershopApi.dto.LoginResponseDTO;
import com.corteBrabo.barbershopApi.dto.SignupRequestDTO;
import com.corteBrabo.barbershopApi.mapper.BusinessMapper;
import com.corteBrabo.barbershopApi.mapper.UserMapper;
import com.corteBrabo.barbershopApi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AuthService {

    public static final int TRIAL_DAYS = 14;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final SlugService slugService;
    private final ServiceSeeder serviceSeeder;
    private final UserMapper userMapper;
    private final BusinessMapper businessMapper;

    public AuthService(UserRepository userRepository,
                       BusinessRepository businessRepository,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       SlugService slugService,
                       ServiceSeeder serviceSeeder,
                       UserMapper userMapper,
                       BusinessMapper businessMapper) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.slugService = slugService;
        this.serviceSeeder = serviceSeeder;
        this.userMapper = userMapper;
        this.businessMapper = businessMapper;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.login().trim(), dto.password())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("E-mail ou senha incorretos", e);
        }

        User user = (User) auth.getPrincipal();
        return toResponse(userRepository.findById(user.getId()).orElseThrow());
    }

    @Transactional
    public LoginResponseDTO signup(SignupRequestDTO dto) {
        String email = dto.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("Já existe uma conta com este e-mail");
        }

        Business business = new Business();
        business.setName(dto.businessName().trim());
        business.setSlug(slugService.uniqueSlugFor(dto.businessName()));
        business.setSegment(dto.segment());
        business.setPhone(dto.telefone());
        business.setEmail(email);
        business.setPlan(BusinessPlan.PROFISSIONAL);
        business.setSubscriptionStatus(SubscriptionStatus.TRIAL);
        business.setTrialEndsAt(LocalDateTime.now().plusDays(TRIAL_DAYS));
        businessRepository.save(business);

        User owner = new User();
        owner.setBusiness(business);
        owner.setName(dto.ownerName().trim());
        owner.setTelefone(dto.telefone());
        owner.setEmail(email);
        owner.setPassword(passwordEncoder.encode(dto.password()));
        owner.setRole(UserRole.OWNER);
        owner.setBookable(true);
        userRepository.save(owner);

        serviceSeeder.seed(business);

        return toResponse(owner);
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO me(User currentUser) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        return new LoginResponseDTO(null, userMapper.toResponseDTO(user), businessMapper.toResponseDTO(user.getBusiness()));
    }

    private LoginResponseDTO toResponse(User user) {
        String token = jwtService.generateToken(user);
        return new LoginResponseDTO(token, userMapper.toResponseDTO(user), businessMapper.toResponseDTO(user.getBusiness()));
    }
}
