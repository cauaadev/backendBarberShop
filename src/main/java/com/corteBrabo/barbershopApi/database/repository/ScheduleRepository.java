package com.corteBrabo.barbershopApi.database.repository;

import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByBarbers_Name(String barberName);
    List<Schedule> findByBarbers_Id(Long barberId);
    List<Schedule> findByClient_Id(Long clientId);
    List<Schedule> findByStatus(ScheduleStatus status);

    boolean existsByClient_IdAndStatusIn(Long clientId, Collection<ScheduleStatus> statuses);
}
