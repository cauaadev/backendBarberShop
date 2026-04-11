package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
