package com.sadik.gimnasio.exception;

public class ClaseNoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClaseNoEncontradaException(Long id) {
        super("No existe la clase con id " + id);
    }
}
