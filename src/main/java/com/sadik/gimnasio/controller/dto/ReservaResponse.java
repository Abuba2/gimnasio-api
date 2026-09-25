package com.sadik.gimnasio.controller.dto;

import java.time.LocalDateTime;

import com.sadik.gimnasio.model.Reserva;

public record ReservaResponse(
        Long id,
        Long socioId,
        String socio,
        Long claseId,
        String clase,
        LocalDateTime fechaClase,
        LocalDateTime fechaReserva,
        String estado) {
    public static ReservaResponse de(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getSocio().getId(),
                reserva.getSocio().getNombre(),
                reserva.getClase().getId(),
                reserva.getClase().getNombre(),
                reserva.getClase().getFechaHora(),
                reserva.getFecha(),
                reserva.getEstado().name());
    }
}
