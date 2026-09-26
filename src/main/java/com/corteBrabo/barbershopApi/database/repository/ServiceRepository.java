package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByBusiness_IdOrderByServiceNameAsc(Long businessId);
    List<Service> findByBusiness_IdAndActiveTrueOrderByServiceNameAsc(Long businessId);
    List<Service> findByBusiness_IdAndServiceIdIn(Long businessId, Collection<Long> ids);
    Optional<Service> findByServiceIdAndBusiness_Id(Long id, Long businessId);
}
