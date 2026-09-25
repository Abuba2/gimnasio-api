package com.sadik.gimnasio.controller.dto;

import java.time.LocalDateTime;

import com.sadik.gimnasio.model.Clase;

public record ClaseResumenResponse(
        Long id,
        String nombre,
        LocalDateTime fechaHora,
        int aforo) {
    public static ClaseResumenResponse de(Clase clase) {
        return new ClaseResumenResponse(
                clase.getId(),
                clase.getNombre(),
                clase.getFechaHora(),
                clase.getAforo());
    }
}
