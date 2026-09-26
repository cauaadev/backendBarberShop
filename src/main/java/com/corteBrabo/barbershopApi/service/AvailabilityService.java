package com.corteBrabo.barbershopApi.service;

import com.corteBrabo.barbershopApi.database.model.Business;
import com.corteBrabo.barbershopApi.database.model.BusinessHours;
import com.corteBrabo.barbershopApi.database.model.Schedule;
import com.corteBrabo.barbershopApi.database.model.ScheduleStatus;
import com.corteBrabo.barbershopApi.database.model.User;
import com.corteBrabo.barbershopApi.database.repository.BusinessHoursRepository;
import com.corteBrabo.barbershopApi.database.repository.ScheduleRepository;
import com.corteBrabo.barbershopApi.dto.AvailabilityDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityService {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final BusinessHoursRepository hoursRepository;
    private final ScheduleRepository scheduleRepository;

    public AvailabilityService(BusinessHoursRepository hoursRepository, ScheduleRepository scheduleRepository) {
        this.hoursRepository = hoursRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<AvailabilityDTO.Slot> slots(Business business, LocalDate date, int durationMinutes, List<User> professionals) {
        if (professionals.isEmpty() || durationMinutes <= 0) return List.of();

        Optional<BusinessHours> hours = hoursRepository.findByBusiness_IdAndDayOfWeek(business.getId(), date.getDayOfWeek().getValue());
        if (hours.isEmpty() || hours.get().isClosed() || hours.get().getOpenTime() == null) return List.of();

        LocalDateTime open = date.atTime(hours.get().getOpenTime());
        LocalDateTime close = date.atTime(hours.get().getCloseTime());
        LocalDateTime earliest = LocalDateTime.now().plusMinutes(business.getMinAdvanceMinutes());

        List<Long> ids = professionals.stream().map(User::getId).toList();
        List<Schedule> busy = scheduleRepository.findOverlapping(ids, ScheduleStatus.BLOCKING, open, close);

        List<AvailabilityDTO.Slot> slots = new ArrayList<>();
        int step = Math.max(5, business.getSlotIntervalMinutes());
        for (LocalDateTime start = open; !start.plusMinutes(durationMinutes).isAfter(close); start = start.plusMinutes(step)) {
            if (start.isBefore(earliest)) continue;
            LocalDateTime end = start.plusMinutes(durationMinutes);
            LocalDateTime slotStart = start;
            List<Long> free = ids.stream()
                    .filter(id -> busy.stream().noneMatch(s -> s.getProfessional().getId().equals(id)
                            && s.getStartAt().isBefore(end) && s.getEndAt().isAfter(slotStart)))
                    .toList();
            if (!free.isEmpty()) slots.add(new AvailabilityDTO.Slot(start.format(TIME), free));
        }
        return slots;
    }
}
