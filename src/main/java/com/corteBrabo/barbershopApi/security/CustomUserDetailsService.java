package com.corteBrabo.barbershopApi.security;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = login.contains("@")
                ? userRepository.findByEmailIgnoreCase(login)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"))
                : findStaffByTelefone(login);

        if (user.getRole().equals(UserRole.CLIENT)) {
            throw new UsernameNotFoundException("Cliente não tem acesso ao sistema");
        }
        return user;
    }

    private User findStaffByTelefone(String telefone) {
        List<User> matches = userRepository.findByTelefoneAndRoleIn(
                telefone.replaceAll("\\D", ""), List.of(UserRole.OWNER, UserRole.PROFESSIONAL));
        if (matches.size() != 1) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
        return matches.get(0);
    }
}
