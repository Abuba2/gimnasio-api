package com.sadik.gimnasio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sadik.gimnasio.exception.EmailDuplicadoException;
import com.sadik.gimnasio.exception.SocioNoEncontradoException;
import com.sadik.gimnasio.model.Socio;
import com.sadik.gimnasio.repository.SocioRepository;

@Service
public class SocioService {
    private final SocioRepository repository;

    public SocioService(SocioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Socio crear(String nombre, String email) {
        if (repository.existsByEmail(email)) {
            throw new EmailDuplicadoException(email);
        }

        Socio socio = new Socio(nombre, email);
        return repository.save(socio);
    }

    @Transactional(readOnly = true)
    public Socio buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new SocioNoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<Socio> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public Socio darDeBaja(Long id) {
        Socio socio = buscarPorId(id);
        socio.setActivo(false);
        return repository.save(socio);
    }
    
    @Transactional(readOnly = true)
    public List<Socio> buscarPorActivo(boolean activo) {
        return repository.findByActivo(activo);
    }
    
}
