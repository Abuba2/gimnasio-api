package com.sadik.gimnasio.controller.dto;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearMonitorRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Size(max = 150, message = "El email no puede pasar de 150 caracteres")
        @Email
        String email,
        
        @NotBlank(message = "La especialidad es obligatorio")
        @Size(max = 60, message = "La especialidad no puede pasar de 60 caracteres")
        String especialidad
        
		) {
}
