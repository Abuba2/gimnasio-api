package com.sadik.gimnasio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sadik.gimnasio.controller.dto.CrearMonitorRequest;
import com.sadik.gimnasio.model.Monitor;
import com.sadik.gimnasio.service.MonitorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/monitores") 
public class MonitorController {

	private final MonitorService service;

	public MonitorController(MonitorService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Monitor> crear(@Valid @RequestBody CrearMonitorRequest request) {
		Monitor creada = service.crear(request.nombre(), request.email(), request.especialidad());
		return ResponseEntity.status(HttpStatus.CREATED).body(creada);
	}

	@GetMapping
	public List<Monitor> listar(@RequestParam(required = false) String especialidad) {
	    if (especialidad == null) {
	        return service.listarTodos();
	    }
	    return service.buscarPorEspecialidad(especialidad);
	}

	@GetMapping("/{id}")
	public Monitor buscarPorId(@PathVariable Long id) {
		return service.buscarPorId(id);
	}
}
