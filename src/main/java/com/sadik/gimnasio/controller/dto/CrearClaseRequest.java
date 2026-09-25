package com.sadik.gimnasio.controller.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearClaseRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
        String nombre,

        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "No se puede programar una clase en el pasado")
        LocalDateTime fechaHora,

        @Min(value = 1, message = "El aforo debe ser al menos 1")
        int aforo,

        @NotNull(message = "Hay que indicar la sala")
        Long salaId,

        @NotNull(message = "Hay que indicar el monitor")
        Long monitorId) {
}
