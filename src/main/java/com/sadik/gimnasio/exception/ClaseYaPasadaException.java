package com.sadik.gimnasio.exception;

public class ClaseYaPasadaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClaseYaPasadaException(Long claseId) {
        super("La clase " + claseId + " ya ha pasado");
    }
}
