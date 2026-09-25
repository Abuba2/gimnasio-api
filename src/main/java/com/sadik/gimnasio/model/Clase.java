package com.sadik.gimnasio.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clase")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 100)
    private String nombre;

    @Setter
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Setter
    @Column(nullable = false)
    private int aforo;

  
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor;

    public Clase(String nombre, LocalDateTime fechaHora, int aforo, Sala sala, Monitor monitor) {
        this.nombre = nombre;
        this.fechaHora = fechaHora;
        this.aforo = aforo;
        this.sala = sala;
        this.monitor = monitor;
    }
}
