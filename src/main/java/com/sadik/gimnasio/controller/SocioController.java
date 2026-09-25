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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sadik.gimnasio.controller.dto.CrearSocioRequest;
import com.sadik.gimnasio.model.Socio;
import com.sadik.gimnasio.service.SocioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/socios")
public class SocioController {
    private final SocioService service;

    public SocioController(SocioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Socio> crear(@Valid @RequestBody CrearSocioRequest request) {
        Socio creado = service.crear(request.nombre(), request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<Socio> listarTodos(@RequestParam(required = false) Boolean activo) {
    	if(activo == null) {
    		return service.listarTodos();
    	}
    	
    	return service.buscarPorActivo(activo);
    	
    }

    @GetMapping("/{id}")
    public Socio buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PatchMapping("/{id}/baja")
    public Socio darDeBaja(@PathVariable Long id) {
        return service.darDeBaja(id);
    }
}
