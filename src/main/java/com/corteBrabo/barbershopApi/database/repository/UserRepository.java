package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.model.UserRole;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findByTelefoneAndRoleIn(String telefone, Collection<UserRole> roles);

    boolean existsByBusiness_IdAndTelefone(Long businessId, String telefone);
    Optional<User> findByBusiness_IdAndTelefone(Long businessId, String telefone);

    Optional<User> findByIdAndBusiness_Id(Long id, Long businessId);
    List<User> findByBusiness_IdAndRoleInOrderByNameAsc(Long businessId, Collection<UserRole> roles);
    List<User> findByBusiness_IdAndBookableTrueAndActiveTrueOrderByNameAsc(Long businessId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> lockById(@Param("id") Long id);

    long countByBusiness_IdAndBookableTrueAndActiveTrue(Long businessId);

    @Query("""
            select u from User u
            where u.business.id = :businessId and u.role = com.corteBrabo.barbershopApi.database.model.UserRole.CLIENT
              and (:search is null or lower(u.name) like lower(concat('%', :search, '%')) or u.telefone like concat('%', :search, '%'))
            order by u.name asc
            """)
    List<User> searchClients(@Param("businessId") Long businessId, @Param("search") String search);

    long countByBusiness_IdAndRoleAndDateBetween(Long businessId, UserRole role, java.time.LocalDate from, java.time.LocalDate to);
}
