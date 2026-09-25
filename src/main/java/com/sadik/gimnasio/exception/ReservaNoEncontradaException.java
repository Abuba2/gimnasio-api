package com.sadik.gimnasio.exception;

public class ReservaNoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReservaNoEncontradaException(Long id) {
        super("No existe la reserva con id " + id);
    }
}
