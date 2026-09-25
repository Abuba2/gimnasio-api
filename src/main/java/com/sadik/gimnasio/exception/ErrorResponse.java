package com.sadik.gimnasio.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int estado,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> campos) {
    public static ErrorResponse of(int estado, String error, String mensaje, String ruta) {
        return new ErrorResponse(LocalDateTime.now(), estado, error, mensaje, ruta, null);
    }

    public static ErrorResponse deValidacion(int estado, String error, String mensaje,
            String ruta, Map<String, String> campos) {
        return new ErrorResponse(LocalDateTime.now(), estado, error, mensaje, ruta, campos);
    }
}
