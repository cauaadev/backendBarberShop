package com.corteBrabo.barbershopApi.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User client;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "schedule_barbers",
            joinColumns = @JoinColumn(name = "scheduleId"),
            inverseJoinColumns = @JoinColumn(name = "barber_id")
    )
    private List<User> barbers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "schedule_services",
            joinColumns = @JoinColumn(name = "scheduleId"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;

    private String status;
    private LocalDateTime date;
}
