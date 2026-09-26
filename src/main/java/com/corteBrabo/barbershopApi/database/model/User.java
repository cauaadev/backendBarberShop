package com.corteBrabo.barbershopApi.database.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="user")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    private String name;

    private String telefone;

    @Column(unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private boolean bookable;

    @Column(nullable = false)
    private boolean active = true;

    private BigDecimal commissionPercent;

    @Column(length = 500)
    private String notes;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDate date;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Long getBusinessId() {
        return business.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
