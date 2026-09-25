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
@Table(name = "sala")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Setter
    @Column(nullable = false)
    private int aforo;

    public Sala(String nombre, int aforo) {
        this.nombre = nombre;
        this.aforo = aforo;
    }
}
