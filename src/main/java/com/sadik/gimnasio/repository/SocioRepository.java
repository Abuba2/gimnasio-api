package com.sadik.gimnasio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sadik.gimnasio.model.Socio;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

	boolean existsByEmail(String email);
	
	List<Socio> findByActivo(boolean activo);

}
