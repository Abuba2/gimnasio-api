package com.sadik.gimnasio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sadik.gimnasio.exception.EmailDuplicadoException;
import com.sadik.gimnasio.exception.MonitorNoEncontradoException;
import com.sadik.gimnasio.model.Monitor;
import com.sadik.gimnasio.repository.MonitorRepository;

@Service
public class MonitorService {
    private final MonitorRepository repository;

    public MonitorService(MonitorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Monitor crear(String nombre, String email, String especialidad) {
        if (repository.existsByEmail(email)) {
            throw new EmailDuplicadoException(email);
        }

        Monitor monitor = new Monitor(nombre, email, especialidad);
        return repository.save(monitor);
    }

    @Transactional(readOnly = true)
    public Monitor buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MonitorNoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<Monitor> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Monitor> buscarPorEspecialidad(String especialidad) {
        return repository.findByEspecialidad(especialidad);
    }
}
