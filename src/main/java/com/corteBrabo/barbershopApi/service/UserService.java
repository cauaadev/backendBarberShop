package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.dto.UserRequestDTO;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.mapper.UserMapper;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userepo;
    @Autowired
    private UserMapper userMapper;

    public User createUser(UserRequestDTO dto) {
        if (userepo.existsByTelefone(dto.getTelefone())) {
            throw new IllegalStateException("Telefone já cadastrado");
        }
        User user = userMapper.toEntity(dto);
        return userepo.saveAndFlush(user);
    }

    public User getbyId(Long id) {
           return userepo.findById(id)
                   .orElseThrow(() -> new NotFoundException("Não encontrado usuario com id " + id));
    }
    public void deleteById(Long id ) {
        if (!userepo.existsById(id)) {
            throw new NotFoundException("Não encontrado usuario com id  " + id);
        }
        userepo.deleteById(id);
    }
    public void updateUserById(Long id, UserRequestDTO dto) {
        User usuarioEncontrado = userepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Não encontrado usuario com id " + id));

        usuarioEncontrado.setName(dto.getName());
        usuarioEncontrado.setTelefone(dto.getTelefone());
        usuarioEncontrado.setRole(dto.getRole());

        userepo.save(usuarioEncontrado);
    }
}
