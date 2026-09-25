package com.sadik.gimnasio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sadik.gimnasio.model.Sala;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    Optional<Sala> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
