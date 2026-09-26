package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByBusiness_IdOrderByPriceAsc(Long businessId);
    Optional<MembershipPlan> findByIdAndBusiness_Id(Long id, Long businessId);
}
