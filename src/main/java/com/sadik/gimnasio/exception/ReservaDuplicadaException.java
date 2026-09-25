package com.sadik.gimnasio.exception;

public class ReservaDuplicadaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReservaDuplicadaException(Long socioId, Long claseId) {
        super("El socio " + socioId + " ya tiene reserva en la clase " + claseId);
    }
}
