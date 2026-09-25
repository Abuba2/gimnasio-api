package com.sadik.gimnasio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sadik.gimnasio.controller.dto.CrearReservaRequest;
import com.sadik.gimnasio.controller.dto.ReservaResponse;
import com.sadik.gimnasio.model.Reserva;
import com.sadik.gimnasio.service.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> reservar(@Valid @RequestBody CrearReservaRequest request) {
        Reserva reserva = service.reservar(request.socioId(), request.claseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ReservaResponse.de(reserva));
    }

    @GetMapping
    public List<ReservaResponse> listar() {
        return service.listarConDetalle();
    }

    @PatchMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
