package com.sadik.gimnasio.controller.dto;

import java.time.LocalDateTime;

import com.sadik.gimnasio.model.Clase;

public record ClaseDetalleResponse(
        Long id,
        String nombre,
        LocalDateTime fechaHora,
        int aforo,
        String sala,
        String monitor) {
    public static ClaseDetalleResponse de(Clase clase) {
        return new ClaseDetalleResponse(
                clase.getId(),
                clase.getNombre(),
                clase.getFechaHora(),
                clase.getAforo(),
                clase.getSala().getNombre(),
                clase.getMonitor().getNombre());
    }
}
