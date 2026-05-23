package com.corteBrabo.barbershopApi.security;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String telefone) throws UsernameNotFoundException {
        User user = userRepository.findByTelefone(telefone)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + telefone));

        if (user.getRole() == UserRole.CLIENT) {
            throw new UsernameNotFoundException("Cliente não tem acesso ao sistema");
        }
        return user;
    }
}
