package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import com.corteBrabo.barbershopApi.exception.UserCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;


@Service
public class UserService {
    @Autowired
    private UserRepository userepo;

    public User createUser(User user) {
        try {
            return userepo.saveAndFlush(user);
        } catch (Exception e) {
            throw new UserCreationException("Erro ao criar usuário");
        }
    }

    public User getbyId(Long id) {
           return userepo.findById(id)
                   .orElseThrow(() -> new NotFoundException("Não encontrado " + id));

    }

    public void deleteById(Long id ) {
        if (!userepo.existsById(id)) {
            throw new NotFoundException("Não encontrado " + id);
        }
        userepo.deleteById(id);
    }

}
