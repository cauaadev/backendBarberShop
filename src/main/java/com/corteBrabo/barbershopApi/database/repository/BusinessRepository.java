package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    boolean existsBySlug(String slug);
    Optional<Business> findBySlug(String slug);
}
