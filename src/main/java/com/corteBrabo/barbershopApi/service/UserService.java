package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.ClientCreateDTO;
import com.corteBrabo.barbershopApi.dto.StaffCreateDTO;
import com.corteBrabo.barbershopApi.dto.UserResponseDTO;
import com.corteBrabo.barbershopApi.dto.UserUpdateDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    public UserResponseDTO createClient(ClientCreateDTO dto) {
        if (userRepository.existsByTelefone(dto.getTelefone())) {
            throw new IllegalStateException("Telefone já cadastrado");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setTelefone(dto.getTelefone());
        user.setRole(UserRole.CLIENT);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO createStaff(StaffCreateDTO dto) {
        if (userRepository.existsByTelefone(dto.getTelefone())) {
            throw new IllegalStateException("Telefone já cadastrado");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setTelefone(dto.getTelefone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + id));
        return userMapper.toResponseDTO(user);
    }

    public List<UserResponseDTO> findAll(UserRole role) {
        List<User> users = (role == null)
                ? userRepository.findAll()
                : userRepository.findByRole(role);
        return users.stream().map(userMapper::toResponseDTO).toList();
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Usuário não encontrado: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserResponseDTO updateUserById(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + id));

        if (!user.getTelefone().equals(dto.getTelefone())
                && userRepository.existsByTelefone(dto.getTelefone())) {
            throw new IllegalStateException("Telefone já cadastrado");
        }

        user.setName(dto.getName());
        user.setTelefone(dto.getTelefone());
        user.setRole(dto.getRole());

        return userMapper.toResponseDTO(userRepository.save(user));
    }
}
