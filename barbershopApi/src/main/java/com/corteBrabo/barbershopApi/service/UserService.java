package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.UserRepository;
import com.corteBrabo.barbershopApi.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userepo;

    public User getbyId(int id) {
       return userepo.findById(id)
               .orElseThrow(() -> new NotFoundException("Não encontrado"+ id));
    }


}
