package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    @Autowired
    private UserRepository userepo;

    public User createUser(User user) {
        return userepo.saveAndFlush(user);
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
