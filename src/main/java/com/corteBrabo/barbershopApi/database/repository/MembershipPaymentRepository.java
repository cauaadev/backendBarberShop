package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.MembershipPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MembershipPaymentRepository extends JpaRepository<MembershipPayment, Long> {
    List<MembershipPayment> findByMembership_IdOrderByPaidAtDesc(Long membershipId);
    List<MembershipPayment> findByMembership_Business_IdAndPaidAtBetween(Long businessId, LocalDateTime from, LocalDateTime to);
}
