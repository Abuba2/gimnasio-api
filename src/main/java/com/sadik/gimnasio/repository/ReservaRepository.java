package com.sadik.gimnasio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sadik.gimnasio.model.Clase;
import com.sadik.gimnasio.model.EstadoReserva;
import com.sadik.gimnasio.model.Reserva;
import com.sadik.gimnasio.model.Socio;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    long countByClaseAndEstado(Clase clase, EstadoReserva estado);

    boolean existsBySocioAndClaseAndEstado(Socio socio, Clase clase, EstadoReserva estado);

    @Query("SELECT r FROM Reserva r JOIN FETCH r.socio JOIN FETCH r.clase")
    List<Reserva> findAllConSocioYClase();
}
