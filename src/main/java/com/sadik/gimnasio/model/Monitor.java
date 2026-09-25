package com.sadik.gimnasio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "monitor")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Monitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Setter
    @Column(nullable = false, length = 60)
    private String especialidad;

    public Monitor(String nombre, String email, String especialidad) {
        this.nombre = nombre;
        this.email = email;
        this.especialidad = especialidad;
    }
}
