package com.sadik.gimnasio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sadik.gimnasio.model.Monitor;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Long> {
    boolean existsByEmail(String email);

    Optional<Monitor> findByEmail(String email);

    List<Monitor> findByEspecialidad(String especialidad);
}
