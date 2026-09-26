package com.corteBrabo.barbershopApi.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "business")
@Getter
@Setter
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 80)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BusinessSegment segment;

    private String phone;
    private String email;
    private String address;
    private String city;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 9)
    private String brandColor = "#6D5DF6";

    @Column(nullable = false)
    private boolean bookingEnabled = true;

    @Column(nullable = false)
    private int slotIntervalMinutes = 30;

    @Column(nullable = false)
    private int minAdvanceMinutes = 60;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusinessPlan plan = BusinessPlan.PROFISSIONAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.TRIAL;

    private LocalDateTime trialEndsAt;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
