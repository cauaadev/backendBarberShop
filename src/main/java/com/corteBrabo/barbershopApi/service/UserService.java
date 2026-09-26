package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Business;
import com.corteBrabo.barbershopApi.database.model.SubscriptionStatus;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.ScheduleRepository;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.PasswordChangeDTO;
import com.corteBrabo.barbershopApi.dto.ProfileUpdateDTO;
import com.corteBrabo.barbershopApi.dto.StaffCreateDTO;
import com.corteBrabo.barbershopApi.dto.UserResponseDTO;
import com.corteBrabo.barbershopApi.dto.UserUpdateDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.exception.PlanLimitException;
import com.corteBrabo.barbershopApi.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private static final List<UserRole> STAFF_ROLES = List.of(UserRole.OWNER, UserRole.PROFESSIONAL);

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final BusinessService businessService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ScheduleRepository scheduleRepository,
                       BusinessService businessService,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.businessService = businessService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findTeam(User currentUser) {
        return userRepository.findByBusiness_IdAndRoleInOrderByNameAsc(currentUser.getBusinessId(), STAFF_ROLES)
                .stream().map(userMapper::toResponseDTO).toList();
    }

    @Transactional
    public UserResponseDTO createStaff(User currentUser, StaffCreateDTO dto) {
        Business business = businessService.load(currentUser.getBusinessId());
        String email = normalizeEmail(dto.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("E-mail já cadastrado");
        }
        if (userRepository.existsByBusiness_IdAndTelefone(business.getId(), dto.telefone())) {
            throw new IllegalStateException("Telefone já cadastrado");
        }
        if (dto.bookable()) ensureProfessionalSlot(business, null);

        User user = new User();
        user.setBusiness(business);
        user.setName(dto.name().trim());
        user.setTelefone(dto.telefone());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setBookable(dto.bookable());
        user.setCommissionPercent(dto.commissionPercent());
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO updateStaff(User currentUser, Long id, UserUpdateDTO dto) {
        User user = loadStaff(currentUser, id);
        String email = normalizeEmail(dto.email());

        if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("E-mail já cadastrado");
        }
        if (!dto.telefone().equals(user.getTelefone())
                && userRepository.existsByBusiness_IdAndTelefone(currentUser.getBusinessId(), dto.telefone())) {
            throw new IllegalStateException("Telefone já cadastrado");
        }
        if (user.getId().equals(currentUser.getId()) && (dto.role() != UserRole.OWNER || !dto.active())) {
            throw new IllegalStateException("Você não pode remover seu próprio acesso de dono");
        }
        boolean willCount = dto.bookable() && dto.active();
        boolean counted = user.isBookable() && user.isActive();
        if (willCount && !counted) ensureProfessionalSlot(businessService.load(currentUser.getBusinessId()), user.getId());

        user.setName(dto.name().trim());
        user.setTelefone(dto.telefone());
        user.setEmail(email);
        user.setRole(dto.role());
        user.setBookable(dto.bookable());
        user.setActive(dto.active());
        user.setCommissionPercent(dto.commissionPercent());
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteStaff(User currentUser, Long id) {
        User user = loadStaff(currentUser, id);
        if (user.getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Você não pode excluir sua própria conta");
        }
        if (scheduleRepository.existsByProfessional_Id(user.getId())) {
            user.setActive(false);
            user.setBookable(false);
            userRepository.save(user);
            return;
        }
        userRepository.delete(user);
    }

    @Transactional
    public UserResponseDTO updateProfile(User currentUser, ProfileUpdateDTO dto) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        String email = normalizeEmail(dto.email());
        if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("E-mail já cadastrado");
        }
        if (!dto.telefone().equals(user.getTelefone())
                && userRepository.existsByBusiness_IdAndTelefone(currentUser.getBusinessId(), dto.telefone())) {
            throw new IllegalStateException("Telefone já cadastrado");
        }
        user.setName(dto.name().trim());
        user.setTelefone(dto.telefone());
        user.setEmail(email);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void changePassword(User currentUser, PasswordChangeDTO dto) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Senha atual incorreta");
        }
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    private void ensureProfessionalSlot(Business business, Long ignoreUserId) {
        if (business.getSubscriptionStatus() == SubscriptionStatus.TRIAL) return;
        long used = userRepository.countByBusiness_IdAndBookableTrueAndActiveTrue(business.getId());
        if (used >= business.getPlan().getMaxProfessionals()) {
            throw new PlanLimitException("Seu plano permite até " + business.getPlan().getMaxProfessionals()
                    + " profissional(is) na agenda. Faça upgrade para adicionar mais.");
        }
    }

    private User loadStaff(User currentUser, Long id) {
        User user = userRepository.findByIdAndBusiness_Id(id, currentUser.getBusinessId())
                .orElseThrow(() -> new NotFoundException("Membro da equipe não encontrado"));
        if (user.getRole() == UserRole.CLIENT) {
            throw new NotFoundException("Membro da equipe não encontrado");
        }
        return user;
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
