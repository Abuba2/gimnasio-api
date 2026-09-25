package com.sadik.gimnasio.controller.dto;

import jakarta.validation.constraints.NotNull;

public record CrearReservaRequest(

        @NotNull(message = "Hay que indicar el socio")
        Long socioId,

        @NotNull(message = "Hay que indicar la clase")
        Long claseId) {
}
