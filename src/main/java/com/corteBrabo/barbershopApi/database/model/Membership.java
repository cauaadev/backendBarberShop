package com.corteBrabo.barbershopApi.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership")
@Getter
@Setter
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private MembershipPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipStatus status;

    @Column(nullable = false)
    private LocalDate startedAt;

    @Column(nullable = false)
    private LocalDate nextChargeDate;

    @Column(nullable = false)
    private int creditsUsed;

    private LocalDateTime canceledAt;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public MembershipStatus getEffectiveStatus() {
        if (status == MembershipStatus.ACTIVE && nextChargeDate.isBefore(LocalDate.now())) {
            return MembershipStatus.PAST_DUE;
        }
        return status;
    }

    public Integer getCreditsRemaining() {
        Integer credits = plan.getMonthlyCredits();
        return credits == null ? null : Math.max(0, credits - creditsUsed);
    }
}
