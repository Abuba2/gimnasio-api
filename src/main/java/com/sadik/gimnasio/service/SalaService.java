package com.sadik.gimnasio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sadik.gimnasio.exception.NombreDeSalaDuplicadoException;
import com.sadik.gimnasio.exception.SalaNoEncontradaException;
import com.sadik.gimnasio.model.Sala;
import com.sadik.gimnasio.repository.SalaRepository;

@Service
public class SalaService {
    private final SalaRepository repository;

    public SalaService(SalaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Sala crear(String nombre, int aforo) {
        if (aforo <= 0) {
            throw new IllegalArgumentException("El aforo debe ser mayor que 0, recibido: " + aforo);
        }

        if (repository.existsByNombre(nombre)) {
            throw new NombreDeSalaDuplicadoException(nombre);
        }

        Sala sala = new Sala(nombre, aforo);
        return repository.save(sala);
    }

    @Transactional(readOnly = true)
    public Sala buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SalaNoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Sala> listarTodas() {
        return repository.findAll();
    }
}
