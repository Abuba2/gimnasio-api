package com.sadik.gimnasio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sadik.gimnasio.model.Clase;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {
    @Query("SELECT c FROM Clase c JOIN FETCH c.sala JOIN FETCH c.monitor")
    List<Clase> findAllConSalaYMonitor();
}
