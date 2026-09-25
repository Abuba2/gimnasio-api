package com.sadik.gimnasio.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sadik.gimnasio.controller.dto.ClaseDetalleResponse;
import com.sadik.gimnasio.exception.ClaseNoEncontradaException;
import com.sadik.gimnasio.model.Clase;
import com.sadik.gimnasio.model.Monitor;
import com.sadik.gimnasio.model.Sala;
import com.sadik.gimnasio.repository.ClaseRepository;

@Service
public class ClaseService {
    private final ClaseRepository repository;
    private final SalaService salaService;
    private final MonitorService monitorService;

    public ClaseService(ClaseRepository repository, SalaService salaService,
            MonitorService monitorService) {
        this.repository = repository;
        this.salaService = salaService;
        this.monitorService = monitorService;
    }

    @Transactional
    public Clase crear(String nombre, LocalDateTime fechaHora, int aforo,
            Long salaId, Long monitorId) {
        Sala sala = salaService.buscarPorId(salaId);
        Monitor monitor = monitorService.buscarPorId(monitorId);

        Clase clase = new Clase(nombre, fechaHora, aforo, sala, monitor);
        return repository.save(clase);
    }

    @Transactional(readOnly = true)
    public Clase buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ClaseNoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Clase> listarTodas() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ClaseDetalleResponse> listarConDetalle() {
        return repository.findAllConSalaYMonitor().stream()
                .map(clase -> ClaseDetalleResponse.de(clase))
                .toList();
    }
}
