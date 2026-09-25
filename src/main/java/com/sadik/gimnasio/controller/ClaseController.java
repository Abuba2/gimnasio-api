package com.sadik.gimnasio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sadik.gimnasio.controller.dto.ClaseDetalleResponse;
import com.sadik.gimnasio.controller.dto.ClaseResumenResponse;
import com.sadik.gimnasio.controller.dto.CrearClaseRequest;
import com.sadik.gimnasio.model.Clase;
import com.sadik.gimnasio.service.ClaseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {
    private final ClaseService service;

    public ClaseController(ClaseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClaseResumenResponse> crear(@Valid @RequestBody CrearClaseRequest request) {
        Clase creada = service.crear(request.nombre(), request.fechaHora(),
                request.aforo(), request.salaId(), request.monitorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ClaseResumenResponse.de(creada));
    }

    @GetMapping
    public List<ClaseResumenResponse> listar() {
        return service.listarTodas().stream()
                .map(clase -> ClaseResumenResponse.de(clase))
                .toList();
    }

    @GetMapping("/detalle")
    public List<ClaseDetalleResponse> listarConDetalle() {
        return service.listarConDetalle();
    }
}
