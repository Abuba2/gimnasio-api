package com.sadik.gimnasio.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sadik.gimnasio.controller.dto.CrearSalaRequest;
import com.sadik.gimnasio.model.Sala;
import com.sadik.gimnasio.service.SalaService;

@RestController
@RequestMapping("/api/salas")
public class SalaController {
    private final SalaService service;

    public SalaController(SalaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Sala> crear(@Valid @RequestBody CrearSalaRequest request) {
        Sala creada = service.crear(request.nombre(), request.aforo());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public List<Sala> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public Sala buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
