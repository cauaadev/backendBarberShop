package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.BusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHours, Long> {
    List<BusinessHours> findByBusiness_IdOrderByDayOfWeekAsc(Long businessId);
    Optional<BusinessHours> findByBusiness_IdAndDayOfWeek(Long businessId, int dayOfWeek);
}
