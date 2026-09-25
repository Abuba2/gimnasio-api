package com.sadik.gimnasio.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sadik.gimnasio.controller.dto.ReservaResponse;
import com.sadik.gimnasio.exception.ClaseCompletaException;
import com.sadik.gimnasio.exception.ClaseYaPasadaException;
import com.sadik.gimnasio.exception.ReservaDuplicadaException;
import com.sadik.gimnasio.exception.ReservaNoEncontradaException;
import com.sadik.gimnasio.exception.SocioInactivoException;
import com.sadik.gimnasio.model.Clase;
import com.sadik.gimnasio.model.EstadoReserva;
import com.sadik.gimnasio.model.Reserva;
import com.sadik.gimnasio.model.Socio;
import com.sadik.gimnasio.repository.ReservaRepository;

@Service
public class ReservaService {
    private final ReservaRepository repository;
    private final SocioService socioService;
    private final ClaseService claseService;

    public ReservaService(ReservaRepository repository, SocioService socioService,
            ClaseService claseService) {
        this.repository = repository;
        this.socioService = socioService;
        this.claseService = claseService;
    }

    @Transactional
    public Reserva reservar(Long socioId, Long claseId) {
        Socio socio = socioService.buscarPorId(socioId);
        Clase clase = claseService.buscarPorId(claseId);

        if (!socio.isActivo()) {
            throw new SocioInactivoException(socioId);
        }

        if (clase.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new ClaseYaPasadaException(claseId);
        }

        if (repository.existsBySocioAndClaseAndEstado(socio, clase, EstadoReserva.CONFIRMADA)) {
            throw new ReservaDuplicadaException(socioId, claseId);
        }

        long confirmadas = repository.countByClaseAndEstado(clase, EstadoReserva.CONFIRMADA);
        int aforo = clase.getAforo();

        if (aforo <= confirmadas) {
            throw new ClaseCompletaException(claseId, aforo);
        }

        Reserva reserva = new Reserva(socio, clase);
        return repository.save(reserva);
    }

    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.cancelar();
        repository.save(reserva);

        return ReservaResponse.de(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarConDetalle() {
        return repository.findAllConSocioYClase().stream()
                .map(reserva -> ReservaResponse.de(reserva))
                .toList();
    }

    @Transactional(readOnly = true)
    public Reserva buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return repository.findAll();
    }
}
