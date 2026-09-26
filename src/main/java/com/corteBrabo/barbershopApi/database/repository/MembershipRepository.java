package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.Membership;
import com.corteBrabo.barbershopApi.database.model.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByBusiness_IdOrderByCreatedAtDesc(Long businessId);
    List<Membership> findByBusiness_IdAndStatus(Long businessId, MembershipStatus status);
    Optional<Membership> findByIdAndBusiness_Id(Long id, Long businessId);
    Optional<Membership> findFirstByClient_IdAndStatusNot(Long clientId, MembershipStatus status);
    List<Membership> findByBusiness_IdAndClient_IdInAndStatusNot(Long businessId, Collection<Long> clientIds, MembershipStatus status);
    boolean existsByPlan_Id(Long planId);
}
