package com.corteBrabo.barbershopApi.database.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;

@Entity
@Table(name="user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String telefone;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDate date;
}
