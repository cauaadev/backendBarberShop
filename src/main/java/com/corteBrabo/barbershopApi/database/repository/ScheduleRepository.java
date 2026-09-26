package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByIdAndBusiness_Id(Long id, Long businessId);

    List<Schedule> findByBusiness_IdAndStartAtBetweenOrderByStartAtAsc(Long businessId, LocalDateTime from, LocalDateTime to);

    List<Schedule> findByBusiness_IdAndProfessional_IdAndStartAtBetweenOrderByStartAtAsc(
            Long businessId, Long professionalId, LocalDateTime from, LocalDateTime to);

    List<Schedule> findByBusiness_IdAndClient_IdOrderByStartAtDesc(Long businessId, Long clientId);

    List<Schedule> findByBusiness_IdAndClient_IdIn(Long businessId, Collection<Long> clientIds);

    boolean existsByClient_Id(Long clientId);

    boolean existsByProfessional_Id(Long professionalId);

    @Query("""
            select s from Schedule s
            where s.professional.id in :professionalIds
              and s.status in :statuses
              and s.startAt < :to and s.endAt > :from
            """)
    List<Schedule> findOverlapping(@Param("professionalIds") Collection<Long> professionalIds,
                                   @Param("statuses") Collection<ScheduleStatus> statuses,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to);
}
