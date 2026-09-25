package com.sadik.gimnasio.exception;

public class SalaNoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SalaNoEncontradaException(Long id) {
        super("No existe la sala con id " + id);
    }
}
