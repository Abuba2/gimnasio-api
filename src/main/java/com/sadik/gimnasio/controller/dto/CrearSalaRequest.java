package com.sadik.gimnasio.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearSalaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
        String nombre,

        @Min(value = 1, message = "El aforo debe ser al menos 1")
        @Max(value = 500, message = "Un aforo de mas de 500 no es realista")
        int aforo) {
}
