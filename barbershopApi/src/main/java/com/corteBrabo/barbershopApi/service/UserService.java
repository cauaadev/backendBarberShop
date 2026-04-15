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

    public void createUser(User user){
        userepo.saveAndFlush(user);
    }

    public User getbyId(int id) {
       return userepo.findById(id)
               .orElseThrow(() -> new NotFoundException("Não encontrado "+ id));
    }
    public void deleteById(int id ){
        if(!userepo.existsById(id)){
            throw new NotFoundException("Não encontrado "+ id);
        }
        userepo.deleteById(id);
    }



}
